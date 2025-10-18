package mx.dgtic.unam.sismus.controller.web;

import mx.dgtic.unam.sismus.dto.CancionResponseDto;
import mx.dgtic.unam.sismus.dto.ListaResponseDto;
import mx.dgtic.unam.sismus.service.CancionService;
import mx.dgtic.unam.sismus.service.ListaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PrincipalController {

    private final CancionService cancionService;
    private final ListaService listaService;

    public PrincipalController(CancionService cancionService, ListaService listaService) {
        this.cancionService = cancionService;
        this.listaService = listaService;
    }

    @GetMapping("/")
    public String mostrarPrincipal(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "q", defaultValue = "") String query,
                                   Model model) {

        Pageable pageable = PageRequest.of(page, 8);
        Page<CancionResponseDto> cancionesPage = cancionService.buscarPorTituloPaginado(query, pageable);
        List<ListaResponseDto> playlists = listaService.obtenerListasPorUsuario("luismgg");

        model.addAttribute("cancionesPage", cancionesPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", cancionesPage.getTotalPages());
        model.addAttribute("query", query);
        model.addAttribute("playlists", playlists);
        model.addAttribute("contenido", "cancion/listar :: fragment");

        return "layout/main";
    }

    @GetMapping("/playlists")
    public String mostrarPlaylists(Model model) {
        List<ListaResponseDto> playlists = listaService.obtenerListasPorUsuario("luismgg");
        model.addAttribute("playlists", playlists);
        model.addAttribute("contenido", "playlist/listar :: fragment");
        return "layout/main";
    }
}
