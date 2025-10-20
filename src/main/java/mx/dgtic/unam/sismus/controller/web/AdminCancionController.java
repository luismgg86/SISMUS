package mx.dgtic.unam.sismus.controller.web;

import mx.dgtic.unam.sismus.dto.CancionRequestDto;
import mx.dgtic.unam.sismus.dto.CancionResponseDto;
import mx.dgtic.unam.sismus.service.ArtistaService;
import mx.dgtic.unam.sismus.service.GeneroService;
import mx.dgtic.unam.sismus.service.CancionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    // ✅ Mostrar todas las canciones
    @GetMapping
    public String listarCanciones(Model model) {
        List<CancionResponseDto> canciones = cancionService.listarTodas();
        model.addAttribute("canciones", canciones);
        model.addAttribute("contenido", "admin/canciones");
        return "layout/main";
    }

    // ✅ Mostrar formulario para nueva canción
    @GetMapping("/nueva")
    public String nuevaCancion(Model model) {
        CancionRequestDto dto = new CancionRequestDto();
        dto.setFechaAlta(LocalDate.now());

        model.addAttribute("cancion", dto);
        model.addAttribute("artistas", artistaService.listarTodos());
        model.addAttribute("generos", generoService.listarTodos());
        model.addAttribute("contenido", "admin/cancion-form");

        return "layout/main";
    }

    // ✅ Guardar canción (DTO + archivo MP3)
    @PostMapping("/guardar")
    public String guardarCancion(@ModelAttribute("cancion") CancionRequestDto dto,
                                 @RequestParam("archivo") MultipartFile archivo) throws IOException {

        dto.setFechaAlta(LocalDate.now()); // Asignamos fecha si no viene del formulario
        cancionService.guardarCancionConArchivo(dto, archivo);

        return "redirect:/admin/canciones";
    }

    // ✅ Eliminar canción
    @GetMapping("/eliminar/{id}")
    public String eliminarCancion(@PathVariable Integer id) {
        cancionService.eliminar(id);
        return "redirect:/admin/canciones";
    }
}
