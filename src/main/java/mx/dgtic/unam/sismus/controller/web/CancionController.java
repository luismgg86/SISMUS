package mx.dgtic.unam.sismus.controller.web;

import jakarta.servlet.http.HttpServletRequest;
import mx.dgtic.unam.sismus.dto.CancionResponseDto;
import mx.dgtic.unam.sismus.exception.CancionNoEncontradaException;
import mx.dgtic.unam.sismus.model.Usuario;
import mx.dgtic.unam.sismus.service.CancionService;
import mx.dgtic.unam.sismus.service.UsuarioCancionService;
import mx.dgtic.unam.sismus.service.UsuarioService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

@Controller
@RequestMapping("/canciones")
public class CancionController {

    private final CancionService cancionService;
    private final UsuarioService usuarioService;
    private final UsuarioCancionService usuarioCancionService;

    public CancionController(CancionService cancionService,
                             UsuarioService usuarioService,
                             UsuarioCancionService usuarioCancionService) {
        this.cancionService = cancionService;
        this.usuarioService = usuarioService;
        this.usuarioCancionService = usuarioCancionService;
    }

    @GetMapping("/buscar")
    public String buscarCanciones(@RequestParam(value = "q", defaultValue = "") String query,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  Model model) {
        Pageable pageable = PageRequest.of(page, 8);
        Page<CancionResponseDto> cancionesPage = cancionService.buscarPorTituloPaginado(query, pageable);

        model.addAttribute("cancionesPage", cancionesPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", cancionesPage.getTotalPages());
        model.addAttribute("query", query);

        return "cancion/listar :: fragment";
    }

    @GetMapping("/{id}/descargar")
    public ResponseEntity<Resource> descargarCancion(@PathVariable Integer id, HttpServletRequest request) {
        CancionResponseDto cancion = cancionService.buscarPorId(id)
                .orElseThrow(() -> new CancionNoEncontradaException("Canción no encontrada con ID: " + id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            usuarioService.buscarPorNickname(auth.getName()).ifPresent(usuarioDto -> {
                Usuario usuario = new Usuario();
                usuario.setId(usuarioDto.getId());
                usuarioCancionService.registrarDescarga(usuario.getId(), cancion.getId());
            });
        }

        Path rutaArchivo = Paths.get("src/main/resources/static" + cancion.getAudio());
        try {
            if (!Files.exists(rutaArchivo)) return ResponseEntity.notFound().build();
            Resource recurso = new UrlResource(rutaArchivo.toUri());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
                    .body(recurso);
        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
