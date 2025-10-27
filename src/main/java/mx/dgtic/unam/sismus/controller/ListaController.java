package mx.dgtic.unam.sismus.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.dgtic.unam.sismus.dto.CancionResponseDto;
import mx.dgtic.unam.sismus.dto.ListaResponseDto;
import mx.dgtic.unam.sismus.dto.UsuarioResponseDto;
import mx.dgtic.unam.sismus.service.CancionService;
import mx.dgtic.unam.sismus.service.ListaService;
import mx.dgtic.unam.sismus.service.UsuarioService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/playlists")
public class ListaController {

    private final ListaService listaService;
    private final CancionService cancionService;
    private final UsuarioService usuarioService;

    public ListaController(ListaService listaService,
                           CancionService cancionService,
                           UsuarioService usuarioService) {
        this.listaService = listaService;
        this.cancionService = cancionService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String mostrarPlaylists(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            String nickname = userDetails.getUsername();
            List<ListaResponseDto> playlists = listaService.obtenerListasPorUsuario(nickname);
            UsuarioResponseDto usuario = usuarioService.buscarPorNickname(nickname).orElse(null);

            model.addAttribute("usuario", usuario);
            model.addAttribute("playlists", playlists);
        }
        model.addAttribute("titulo", "Tus Playlists");
        model.addAttribute("contenido", "playlist/listar");
        return "layout/main";
    }


    @GetMapping("/{id}")
    public String verPlaylist(@PathVariable Integer id, Model model) {
        ListaResponseDto playlist = listaService.obtenerConRelaciones(id);
        if (playlist != null && playlist.getCanciones() != null) {
            List<Integer> idsActivos = obtenerIdsCancionesActivas();
            playlist.setCanciones(playlist.getCanciones().stream()
                    .filter(c -> idsActivos.contains(c.getId()))
                    .toList());
        }
        Pageable pageable = PageRequest.of(0, 1000);
        List<CancionResponseDto> cancionesDisponibles =
                cancionService.buscarPorTituloActivoPaginado(null, pageable).getContent();
        model.addAttribute("playlist", playlist);
        model.addAttribute("titulo", "Detalle playlist");
        model.addAttribute("cancionesDisponibles", cancionesDisponibles);
        model.addAttribute("contenido", "playlist/detalle");
        return "layout/main";
    }

    @PostMapping("/{playlistId}/agregar-cancion")
    public String agregarCancionDesdeListado(@PathVariable Integer playlistId,
                                             @RequestParam Integer cancionId,
                                             RedirectAttributes redirectAttrs,
                                             HttpServletRequest request) {
        try {
            listaService.agregarCancionALista(playlistId, cancionId);
            redirectAttrs.addFlashAttribute("exito", "Canción agregada correctamente a la playlist.");
        } catch (RuntimeException ex) {
            redirectAttrs.addFlashAttribute("error", ex.getMessage());
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @PostMapping("/{id}/eliminar-cancion")
    public String eliminarCancionDePlaylist(@PathVariable Integer id,
                                            @RequestParam Integer cancionId,
                                            RedirectAttributes redirectAttrs) {
        try {
            listaService.eliminarCancionDeLista(id, cancionId);
            redirectAttrs.addFlashAttribute("exito", "Canción eliminada correctamente.");
        } catch (RuntimeException ex) {
            redirectAttrs.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/playlists/" + id;
    }

    @PostMapping("/{id}/descargar")
    public String descargarPlaylist(@PathVariable Integer id,
                                    RedirectAttributes redirectAttrs,
                                    HttpServletResponse response) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String nickname = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName()))
                ? auth.getName()
                : null;
        try {
            listaService.descargarPlaylistComoZip(id, nickname, response);
        } catch (RuntimeException ex) {
            redirectAttrs.addFlashAttribute("error", ex.getMessage());
            return "redirect:/playlists";
        }
        return null;
    }

    private List<Integer> obtenerIdsCancionesActivas() {
        Pageable pageable = PageRequest.of(0, 10000);
        return cancionService.buscarPorTituloActivoPaginado(null, pageable)
                .map(CancionResponseDto::getId)
                .toList();
    }
}
