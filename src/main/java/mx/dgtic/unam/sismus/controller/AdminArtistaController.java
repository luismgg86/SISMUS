package mx.dgtic.unam.sismus.controller;

import mx.dgtic.unam.sismus.dto.ArtistaDto;
import mx.dgtic.unam.sismus.exception.ArtistaNoEncontradoException;
import mx.dgtic.unam.sismus.exception.EntidadDuplicadaException;
import mx.dgtic.unam.sismus.service.ArtistaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/artistas")
public class AdminArtistaController {

    private final ArtistaService artistaService;

    public AdminArtistaController(ArtistaService artistaService) {
        this.artistaService = artistaService;
    }

    @GetMapping
    public String listarArtistas(Model model) {
        List<ArtistaDto> artistas = artistaService.listarActivos();
        model.addAttribute("artistas", artistas);
        model.addAttribute("contenido", "admin/artistas");
        return "layout/main";
    }

    @GetMapping("/nuevo")
    public String nuevoArtista(Model model) {
        model.addAttribute("modo", "nuevo");
        model.addAttribute("artista", new ArtistaDto());
        model.addAttribute("contenido", "admin/artista-form");
        return "layout/main";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("artista") ArtistaDto dto,
                          @RequestParam(value = "id", required = false) Integer id,
                          Model model) {
        try {
            if (id != null) {
                artistaService.actualizar(id, dto);
            } else {
                artistaService.guardar(dto);
            }
            return "redirect:/admin/artistas";
        } catch (EntidadDuplicadaException ex) {
            model.addAttribute("modo", id != null ? "editar" : "nuevo");
            model.addAttribute("errorMensaje", ex.getMessage());
            model.addAttribute("artista", dto);
            model.addAttribute("contenido", "admin/artista-form");
            return "layout/main";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        ArtistaDto artista = artistaService.buscarPorId(id)
                .orElseThrow(() -> new ArtistaNoEncontradoException("Artista con ID " + id + " no encontrado"));

        model.addAttribute("modo", "editar");
        model.addAttribute("id", id);
        model.addAttribute("artista", artista);
        model.addAttribute("contenido", "admin/artista-form");
        return "layout/main";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        artistaService.eliminar(id);
        return "redirect:/admin/artistas";
    }

    @GetMapping("/inactivos")
    public String listarInactivos(Model model) {
        List<ArtistaDto> artistas = artistaService.listarInactivos();
        model.addAttribute("artistas", artistas);
        model.addAttribute("tituloSeccion", "Artistas Inactivos");
        model.addAttribute("contenido", "admin/artistas");
        return "layout/main";
    }

    @GetMapping("/activar/{id}")
    public String activar(@PathVariable Integer id) {
        artistaService.activar(id);
        return "redirect:/admin/artistas/inactivos";
    }


}
