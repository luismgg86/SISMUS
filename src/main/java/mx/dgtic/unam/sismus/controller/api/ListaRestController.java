package mx.dgtic.unam.sismus.controller.api;

import mx.dgtic.unam.sismus.dto.ListaRequestDto;
import mx.dgtic.unam.sismus.dto.ListaResponseDto;
import mx.dgtic.unam.sismus.service.ListaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class ListaRestController {

    private final ListaService listaService;

    public ListaRestController(ListaService listaService) {
        this.listaService = listaService;
    }

    @PostMapping
    public ResponseEntity<ListaResponseDto> crearPlaylist(@RequestBody ListaRequestDto dto) {
        ListaResponseDto nueva = listaService.crearPlaylist(dto);
        return ResponseEntity.ok(nueva);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListaResponseDto> obtenerPlaylist(@PathVariable Integer id) {
        return ResponseEntity.ok(listaService.obtenerConRelaciones(id));
    }

    @GetMapping("/usuario/{nickname}")
    public ResponseEntity<List<ListaResponseDto>> obtenerPorUsuario(@PathVariable String nickname) {
        return ResponseEntity.ok(listaService.obtenerListasPorUsuario(nickname));
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
}
