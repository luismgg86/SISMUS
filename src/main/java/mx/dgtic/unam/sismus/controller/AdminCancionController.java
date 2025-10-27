package mx.dgtic.unam.sismus.controller;

import mx.dgtic.unam.sismus.dto.CancionRequestDto;
import mx.dgtic.unam.sismus.dto.CancionResponseDto;
import mx.dgtic.unam.sismus.exception.CancionNoEncontradaException;
import mx.dgtic.unam.sismus.service.ArtistaService;
import mx.dgtic.unam.sismus.service.GeneroService;
import mx.dgtic.unam.sismus.service.CancionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/canciones")
public class AdminCancionController {

    private final CancionService cancionService;
    private final ArtistaService artistaService;
    private final GeneroService generoService;

    public AdminCancionController(CancionService cancionService,
                                  ArtistaService artistaService,
                                  GeneroService generoService) {
        this.cancionService = cancionService;
        this.artistaService = artistaService;
        this.generoService = generoService;
    }

    @GetMapping
    public String listarCanciones(Model model) {
        List<CancionResponseDto> canciones = cancionService.listarTodas();
        model.addAttribute("titulo", "Administrar canciones");
        model.addAttribute("canciones", canciones);
        model.addAttribute("contenido", "admin/canciones");
        return "layout/main";
    }

    @GetMapping("/inactivas")
    public String listarInactivas(Model model) {
        List<CancionResponseDto> canciones = cancionService.listarInactivas();
        model.addAttribute("titulo", "Canciones inactivas");
        model.addAttribute("canciones", canciones);
        model.addAttribute("contenido", "admin/canciones-inactivas");
        return "layout/main";
    }

    @GetMapping("/reactivar/{id}")
    public String reactivarCancion(@PathVariable Integer id) {
        cancionService.reactivarCancion(id);
        return "redirect:/admin/canciones/inactivas";
    }

    @GetMapping("/nueva")
    public String nuevaCancion(Model model) {
        CancionRequestDto dto = new CancionRequestDto();
        dto.setFechaAlta(LocalDate.now());
        model.addAttribute("titulo", "Nueva canción");
        model.addAttribute("cancion", dto);
        model.addAttribute("artistas", artistaService.listarTodos());
        model.addAttribute("generos", generoService.listarTodos());
        model.addAttribute("contenido", "admin/cancion-form");
        return "layout/main";
    }

    @PostMapping("/guardar")
    public String guardarCancion(@ModelAttribute("cancion") CancionRequestDto dto,
                                 @RequestParam(value = "archivo", required = false) MultipartFile archivo,
                                 @RequestParam(value = "id", required = false) Integer id,
                                 RedirectAttributes redirectAttrs) {
        try {
            dto.setFechaAlta(LocalDate.now());
            if (id != null) {
                cancionService.actualizarCancion(id, dto, archivo);
            } else {
                cancionService.guardarCancionConArchivo(dto, archivo);
            }
            redirectAttrs.addFlashAttribute("mensajeExito", "Canción guardada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/admin/canciones/nueva";
        } catch (IOException e) {
            redirectAttrs.addFlashAttribute("mensajeError", "Error al guardar el archivo de audio.");
            return "redirect:/admin/canciones/nueva";
        }
        return "redirect:/admin/canciones";
    }

    @GetMapping("/editar/{id}")
    public String editarCancion(@PathVariable Integer id, Model model) {
        CancionResponseDto existente = cancionService.buscarPorId(id)
                .orElseThrow(() -> new CancionNoEncontradaException("Canción con id: " + id + " no encontrada"));

        CancionRequestDto dto = new CancionRequestDto();
        dto.setTitulo(existente.getTitulo());
        dto.setDuracion(existente.getDuracion());
        dto.setAudio(existente.getAudio());
        dto.setFechaAlta(existente.getFechaAlta());
        dto.setArtistaId(existente.getArtistaId());
        dto.setGeneroId(existente.getGeneroId());

        model.addAttribute("titulo", "Editar canción");
        model.addAttribute("modo", "editar");
        model.addAttribute("id", id);
        model.addAttribute("cancion", dto);
        model.addAttribute("artistas", artistaService.listarTodos());
        model.addAttribute("generos", generoService.listarTodos());
        model.addAttribute("contenido", "admin/cancion-form");

        return "layout/main";
    }


    @GetMapping("/eliminar/{id}")
    public String eliminarCancion(@PathVariable Integer id) {
        cancionService.eliminar(id);
        return "redirect:/admin/canciones";
    }
}
