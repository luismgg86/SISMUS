package mx.dgtic.unam.sismus.controller.web;

import jakarta.servlet.http.HttpServletResponse;
import mx.dgtic.unam.sismus.dto.*;
import mx.dgtic.unam.sismus.exception.UsuarioNoEncontradoException;
import mx.dgtic.unam.sismus.service.CancionService;
import mx.dgtic.unam.sismus.service.ListaService;
import mx.dgtic.unam.sismus.service.UsuarioService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
        ListaResponseDto playlist = listaService.obtenerConRelaciones(id);

        if (playlist != null && playlist.getCanciones() != null) {
            List<Integer> idsActivos = obtenerIdsCancionesActivas();
            playlist.setCanciones(
                    playlist.getCanciones().stream()
                            .filter(c -> idsActivos.contains(c.getId()))
                            .toList()
            );
        }

        Pageable pageable = PageRequest.of(0, 1000);
        List<CancionResponseDto> cancionesDisponibles =
                cancionService.buscarPorTituloActivoPaginado(null, pageable).getContent();

        model.addAttribute("playlist", playlist);
        model.addAttribute("cancionesDisponibles", cancionesDisponibles);
        model.addAttribute("contenido", "playlist/detalle");

        return "layout/main";
    }

    @GetMapping("/{id}/fragmento")
    public String obtenerFragmentoPlaylist(@PathVariable Integer id, Model model) {
        ListaResponseDto playlist = listaService.obtenerConRelaciones(id);

        if (playlist != null && playlist.getCanciones() != null) {
            List<Integer> idsActivos = obtenerIdsCancionesActivas();
            playlist.setCanciones(
                    playlist.getCanciones().stream()
                            .filter(c -> idsActivos.contains(c.getId()))
                            .toList()
            );
        }

        Pageable pageable = PageRequest.of(0, 1000);
        List<CancionResponseDto> cancionesDisponibles =
                cancionService.buscarPorTituloActivoPaginado(null, pageable).getContent();

        model.addAttribute("playlist", playlist);
        model.addAttribute("cancionesDisponibles", cancionesDisponibles);
        return "playlist/detalle :: fragmentoTabla";
    }

    @PostMapping("/crear")
    public String crearPlaylist(@RequestParam String nombre,
                                @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            throw new RuntimeException("No hay usuario autenticado");
        }

        String nickname = userDetails.getUsername();

        var usuario = usuarioService.buscarPorNickname(nickname)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + nickname));

        ListaRequestDto dto = new ListaRequestDto();
        dto.setNombre(nombre);
        dto.setUsuarioId(usuario.getId());

        listaService.crearPlaylist(dto);

        return "redirect:/playlists";
    }

    @PostMapping("/{id}/descargar")
    public String descargarPlaylist(@PathVariable Integer id,
                                    RedirectAttributes redirectAttributes,
                                    HttpServletResponse response) throws IOException {

        ListaResponseDto playlist = listaService.obtenerConRelaciones(id);

        if (playlist == null || playlist.getCanciones() == null || playlist.getCanciones().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La playlist está vacía");
            return "redirect:/playlists";
        }

        // 🔹 Filtramos solo las canciones activas y con artista activo
        List<Integer> idsActivos = obtenerIdsCancionesActivas();
        List<CancionResponseDto> cancionesFiltradas = playlist.getCanciones().stream()
                .filter(c -> idsActivos.contains(c.getId()))
                .toList();

        if (cancionesFiltradas.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No hay canciones activas para descargar.");
            return "redirect:/playlists";
        }

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + playlist.getNombre() + ".zip\"");

        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
            for (CancionResponseDto cancion : cancionesFiltradas) {
                Path path = Paths.get("src/main/resources/static" + cancion.getAudio());
                if (Files.exists(path)) {
                    zos.putNextEntry(new ZipEntry(Path.of(cancion.getAudio()).getFileName().toString()));
                    Files.copy(path, zos);
                    zos.closeEntry();
                }
            }
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
