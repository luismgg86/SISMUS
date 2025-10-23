package mx.dgtic.unam.sismus.controller.api;

import mx.dgtic.unam.sismus.dto.CancionResponseDto;
import mx.dgtic.unam.sismus.dto.ListaResponseDto;
import mx.dgtic.unam.sismus.service.CancionService;
import mx.dgtic.unam.sismus.service.ListaService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class ListaRestController {

    private final ListaService listaService;
    private final CancionService cancionService;

    public ListaRestController(ListaService listaService, CancionService cancionService) {
        this.listaService = listaService;
        this.cancionService = cancionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListaResponseDto> obtenerPlaylist(@PathVariable Integer id) {
        ListaResponseDto playlist = listaService.obtenerConRelaciones(id);
        filtrarYContarCancionesActivas(playlist);
        return ResponseEntity.ok(playlist);
    }

    @GetMapping("/usuario/{nickname}")
    public ResponseEntity<List<ListaResponseDto>> obtenerPorUsuario(@PathVariable String nickname) {
        List<ListaResponseDto> playlists = listaService.obtenerListasPorUsuario(nickname);

        // 🔹 Aplicamos el filtro a cada playlist
        playlists.forEach(this::filtrarYContarCancionesActivas);

        return ResponseEntity.ok(playlists);
    }

    @PostMapping("/{id}/agregar-cancion")
    public ResponseEntity<String> agregarCancion(@PathVariable Integer id,
                                                 @RequestParam Integer cancionId) {
        listaService.agregarCancionALista(id, cancionId);
        return ResponseEntity.ok("Canción agregada correctamente a la playlist.");
    }

    @DeleteMapping("/{id}/eliminar-cancion")
    public ResponseEntity<String> eliminarCancion(@PathVariable Integer id,
                                                  @RequestParam Integer cancionId) {
        listaService.eliminarCancionDeLista(id, cancionId);
        return ResponseEntity.ok("Canción eliminada correctamente de la playlist.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarPlaylist(@PathVariable Integer id) {
        listaService.eliminar(id);
        return ResponseEntity.ok("Playlist eliminada correctamente.");
    }

    private void filtrarYContarCancionesActivas(ListaResponseDto playlist) {
        if (playlist == null || playlist.getCanciones() == null) return;

        Pageable pageable = PageRequest.of(0, 10000);
        List<Integer> idsActivos = cancionService.buscarPorTituloActivoPaginado(null, pageable)
                .map(CancionResponseDto::getId)
                .toList();

        playlist.setCanciones(
                playlist.getCanciones().stream()
                        .filter(c -> idsActivos.contains(c.getId()))
                        .toList()
        );

        playlist.setTotalCancionesActivas(playlist.getCanciones().size());
    }
}
