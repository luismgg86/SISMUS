package mx.dgtic.unam.sismus.controller.web;

import jakarta.servlet.http.HttpServletRequest;
import mx.dgtic.unam.sismus.exception.CancionNoEncontradaException;
import mx.dgtic.unam.sismus.model.Cancion;
import mx.dgtic.unam.sismus.model.Usuario;
import mx.dgtic.unam.sismus.model.UsuarioCancion;
import mx.dgtic.unam.sismus.repository.UsuarioRepository;
import mx.dgtic.unam.sismus.service.CancionService;
import mx.dgtic.unam.sismus.service.UsuarioCancionService;
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
    private final UsuarioRepository usuarioRepository;
    private final UsuarioCancionService usuarioCancionService;

    public CancionController(CancionService cancionService,
                             UsuarioRepository usuarioRepository,
                             UsuarioCancionService usuarioCancionService) {
        this.cancionService = cancionService;
        this.usuarioRepository = usuarioRepository;
        this.usuarioCancionService = usuarioCancionService;
    }

    // Fragmento de búsqueda
    @GetMapping("/buscar")
    public String buscarCanciones(
            @RequestParam(value = "q", defaultValue = "") String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        int pageSize = 8;
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<?> cancionesPage = cancionService.buscarPorTituloPaginado(query, pageable);

        model.addAttribute("cancionesPage", cancionesPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", cancionesPage.getTotalPages());
        model.addAttribute("query", query);

        return "cancion/listar :: fragment";
    }

    //Descarga de canción y registro de descarga
    @GetMapping("/{id}/descargar")
    public ResponseEntity<Resource> descargarCancion(@PathVariable Integer id, HttpServletRequest request) {
        Cancion cancion = cancionService.buscarPorId(id)
                .orElseThrow(() -> new CancionNoEncontradaException("Canción no encontrada con ID: " + id));

        // Registrar descarga (si hay usuario autenticado)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            Usuario usuario = usuarioRepository.findByNickname(auth.getName()).orElse(null);
            if (usuario != null) {
                UsuarioCancion registro = new UsuarioCancion();
                registro.setUsuario(usuario);
                registro.setCancion(cancion);
                registro.setFechaDescarga(LocalDate.now());
                usuarioCancionService.registrarDescarga(registro);
            }
        }

        String rutaRelativa = cancion.getAudio();
        Path rutaArchivo = Paths.get("src/main/resources/static" + rutaRelativa);

        try {
            if (!Files.exists(rutaArchivo)) {
                return ResponseEntity.notFound().build();
            }

            Resource recurso = new UrlResource(rutaArchivo.toUri());
            String nombreArchivo = recurso.getFilename();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + nombreArchivo + "\"")
                    .body(recurso);

        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
