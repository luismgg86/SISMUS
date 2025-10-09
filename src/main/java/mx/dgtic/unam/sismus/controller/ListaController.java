package mx.dgtic.unam.sismus.controller;

import mx.dgtic.unam.sismus.model.Cancion;
import mx.dgtic.unam.sismus.model.Lista;
import mx.dgtic.unam.sismus.model.Usuario;
import mx.dgtic.unam.sismus.service.CancionService;
import mx.dgtic.unam.sismus.service.ListaService;
import mx.dgtic.unam.sismus.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/playlists")
public class ListaController {

    private final ListaService listaService;
    private final CancionService cancionService;
    private final UsuarioService usuarioService;

    public ListaController(ListaService listaService, CancionService cancionService, UsuarioService usuarioService) {
        this.listaService = listaService;
        this.cancionService = cancionService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{id}")
    public String verPlaylist(@PathVariable Integer id, Model model) {
        Lista playlist = listaService.obtenerConRelaciones(id);

        if (playlist == null) {
            throw new IllegalArgumentException("Playlist no encontrada con id: " + id);
        }

        List<Cancion> cancionesDisponibles = cancionService.listarTodas();

        model.addAttribute("playlist", playlist);
        model.addAttribute("cancionesDisponibles", cancionesDisponibles);
        model.addAttribute("contenido", "paginas/detallePlaylist :: fragment");
        return "layout/main";
    }

    @PostMapping("/{id}/agregar-cancion")
    public String agregarCancion(@PathVariable Integer id, @RequestParam Integer cancionId) {
        listaService.agregarCancionALista(id, cancionId);
        return "redirect:/playlists/" + id;
    }

    @PostMapping("/{id}/eliminar-cancion")
    public String eliminarCancion(@PathVariable Integer id, @RequestParam Integer cancionId) {
        listaService.eliminarCancionDeLista(id, cancionId);
        return "redirect:/playlists/" + id;
    }

    @PostMapping("/crear")
    public String crearPlaylist(@RequestParam String nombre) {
        // Simulando usuario logueado con id = 1 ya que no hemos visto sesiones
        Usuario usuario = usuarioService.buscarPorId(1)
                .orElseThrow(() -> new IllegalArgumentException("Usuario con id 1 no encontrado"));

        Lista nueva = new Lista();
        nueva.setNombre(nombre);
        nueva.setFechaCreacion(LocalDate.now());
        nueva.setUsuario(usuario);

        listaService.guardar(nueva);

        return "redirect:/playlists";
    }

    @DeleteMapping("/{id}/eliminar")
    @ResponseBody
    public ResponseEntity<String> eliminarPlaylist(@PathVariable Integer id) {
        try {
            // Buscar la playlist por id
            Lista playlist = listaService.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Playlist no encontrada con id: " + id));

            listaService.eliminar(playlist.getId());

            return ResponseEntity.ok("Playlist eliminada correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la playlist: " + e.getMessage());
        }
    }

}
