package mx.dgtic.unam.sismus.controller;

import mx.dgtic.unam.sismus.dto.ListaResponseDto;
import mx.dgtic.unam.sismus.service.CancionService;
import mx.dgtic.unam.sismus.service.ListaService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   @ModelAttribute("exito") String exito,
                                   @ModelAttribute("error") String error,
                                   Model model) {
        Pageable pageable = PageRequest.of(page, 8);
        var cancionesPage = cancionService.buscarPorTituloActivoPaginado(query, pageable);
        List<ListaResponseDto> playlists = null;
        if (userDetails != null) {
            String nickname = userDetails.getUsername();
            playlists = listaService.obtenerListasPorUsuario(nickname);
        }
        model.addAttribute("titulo", "Inicio");
        model.addAttribute("cancionesPage", cancionesPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", cancionesPage.getTotalPages());
        model.addAttribute("query", query);
        model.addAttribute("playlists", playlists);
        model.addAttribute("exito", exito);
        model.addAttribute("error", error);
        model.addAttribute("contenido", "cancion/listar");
        return "layout/main";
    }
}
