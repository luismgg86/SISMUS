package mx.dgtic.unam.sismus.controller.api;

import mx.dgtic.unam.sismus.dto.ListaResponseDto;
import mx.dgtic.unam.sismus.service.ListaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/playlists")
public class ListaRestController {

    private final ListaService listaService;

    public ListaRestController(ListaService listaService) {
        this.listaService = listaService;
    }

    @PostMapping("/{playlistId}/agregar-cancion")
    public ResponseEntity<String> agregarCancionAPlaylist(
            @PathVariable Integer playlistId,
            @RequestParam Integer cancionId) {
        try {
            listaService.agregarCancionALista(playlistId, cancionId);
            return ResponseEntity.ok("Canción agregada correctamente.");
        } catch (IllegalArgumentException e) {
            // Por ejemplo, si el ID no existe
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Playlist o canción no encontrada.");
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("ya está")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("La canción ya está en la playlist.");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al agregar la canción.");
        }
    }

    @DeleteMapping("/{playlistId}/eliminar-cancion")
    public ResponseEntity<String> eliminarCancionDePlaylist(
            @PathVariable Integer playlistId,
            @RequestParam Integer cancionId) {
        try {
            listaService.eliminarCancionDeLista(playlistId, cancionId);
            return ResponseEntity.ok("Canción eliminada correctamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Playlist o canción no encontrada.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la canción de la playlist.");
        }
    }
}
