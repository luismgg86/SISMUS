package mx.dgtic.unam.sismus.controller;

import mx.dgtic.unam.sismus.dto.GeneroDto;
import mx.dgtic.unam.sismus.exception.EntidadDuplicadaException;
import mx.dgtic.unam.sismus.service.GeneroService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/generos")
public class AdminGeneroController {

    private final GeneroService generoService;

    public AdminGeneroController(GeneroService generoService) {
        this.generoService = generoService;
    }

    @GetMapping
    public String listar(Model model) {
        List<GeneroDto> generos = generoService.listarTodos();
        model.addAttribute("titulo", "Administrar géneros");
        model.addAttribute("generos", generos);
        model.addAttribute("contenido", "admin/generos");
        return "layout/main";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("titulo", "Nuevo género");
        model.addAttribute("modo", "nuevo");
        model.addAttribute("genero", new GeneroDto());
        model.addAttribute("contenido", "admin/genero-form");
        return "layout/main";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("genero") GeneroDto dto,
                          @RequestParam(value = "id", required = false) Integer id,
                          Model model) {
        try {
            if (id != null) {
                generoService.actualizar(id, dto);
            } else {
                generoService.guardar(dto);
            }
            return "redirect:/admin/generos";
        } catch (EntidadDuplicadaException ex) {
            model.addAttribute("errorMensaje", ex.getMessage());
            model.addAttribute("modo", id != null ? "editar" : "nuevo");
            model.addAttribute("genero", dto);
            model.addAttribute("contenido", "admin/genero-form");
            return "layout/main";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        GeneroDto genero = generoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Género con id " + id + " no encontrado"));
        model.addAttribute("titulo", "Editar género");
        model.addAttribute("modo", "editar");
        model.addAttribute("id", id);
        model.addAttribute("genero", genero);
        model.addAttribute("contenido", "admin/genero-form");
        return "layout/main";
    }
}
