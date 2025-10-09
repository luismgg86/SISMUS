package mx.dgtic.unam.sismus.controller;

import mx.dgtic.unam.sismus.service.CancionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CancionController {

    private final CancionService cancionService;

    public CancionController(CancionService cancionService) {
        this.cancionService = cancionService;
    }

    @GetMapping("/canciones/buscar")
    public String buscarCanciones(
            @RequestParam(value = "q", defaultValue = "") String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        int pageSize = 8; // Canciones por página
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<?> cancionesPage = cancionService.buscarPorTituloPaginado(query, pageable);

        model.addAttribute("cancionesPage", cancionesPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", cancionesPage.getTotalPages());
        model.addAttribute("query", query);

        return "paginas/canciones :: fragment";
    }
}
