package mx.dgtic.unam.sismus.controller.api;

import mx.dgtic.unam.sismus.service.ListaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/playlists")
public class ListaRestController {

    private final ListaService listaService;

    public ListaRestController(ListaService listaService) {
        this.listaService = listaService;
    }

    @PostMapping("/{playlistId}/agregar-cancion")
    public ResponseEntity<Object> agregarCancionAPlaylist(@PathVariable Integer playlistId,
                                                          @RequestParam Integer cancionId) {
        listaService.agregarCancionALista(playlistId, cancionId);
        return ResponseEntity.ok(Map.of("mensaje", "Canción agregada correctamente."));
    }

    @DeleteMapping("/{playlistId}/eliminar-cancion")
    public ResponseEntity<Object> eliminarCancionDePlaylist(@PathVariable Integer playlistId,
                                                            @RequestParam Integer cancionId) {
        listaService.eliminarCancionDeLista(playlistId, cancionId);
        return ResponseEntity.ok(Map.of("mensaje", "Canción eliminada correctamente."));
    }

    @DeleteMapping("/{playlistId}/eliminar")
    public ResponseEntity<Object> eliminarPlaylist(@PathVariable Integer playlistId) {
        listaService.eliminar(playlistId);
        return ResponseEntity.ok(Map.of("mensaje", "Playlist eliminada correctamente."));
    }

    @PostMapping("/crear")
    public ResponseEntity<Object> crearPlaylist(@RequestParam String nombre,
                                                @RequestParam(required = false) Integer usuarioId) {
        listaService.crearPlaylistVacia(nombre, usuarioId);
        return ResponseEntity.ok(Map.of("mensaje", "Playlist creada correctamente."));
    }



}
