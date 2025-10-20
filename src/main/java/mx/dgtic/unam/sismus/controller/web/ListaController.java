package mx.dgtic.unam.sismus.controller.web;

import jakarta.servlet.http.HttpServletResponse;
import mx.dgtic.unam.sismus.dto.*;
import mx.dgtic.unam.sismus.exception.ListaNoEncontradaException;
import mx.dgtic.unam.sismus.exception.UsuarioNoEncontradoException;
import mx.dgtic.unam.sismus.service.CancionService;
import mx.dgtic.unam.sismus.service.ListaService;
import mx.dgtic.unam.sismus.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
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
        List<CancionResponseDto> cancionesDisponibles = cancionService.listarTodas();

        model.addAttribute("playlist", playlist);
        model.addAttribute("cancionesDisponibles", cancionesDisponibles);
        model.addAttribute("contenido", "playlist/detalle");

        return "layout/main";
    }


    @GetMapping("/{id}/fragmento")
    public String obtenerFragmentoPlaylist(@PathVariable Integer id, Model model) {
        ListaResponseDto playlist = listaService.obtenerConRelaciones(id);
        List<CancionResponseDto> cancionesDisponibles = cancionService.listarTodas();

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
            redirectAttributes.addFlashAttribute("error", "⚠ La playlist está vacía o no existe.");
            return "redirect:/playlists";
        }

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + playlist.getNombre() + ".zip\"");

        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
            for (CancionResponseDto cancion : playlist.getCanciones()) {
                Path path = Paths.get("src/main/resources/static/" + cancion.getAudio());
                if (Files.exists(path)) {
                    zos.putNextEntry(new ZipEntry(cancion.getAudio()));
                    Files.copy(path, zos);
                    zos.closeEntry();
                }
            }
        }

        return null;
    }

    //        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        Usuario usuario = usuarioService.buscarPorNickname(auth.getName())
//                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
//        dto.setUsuarioId(usuario.getId());
}
