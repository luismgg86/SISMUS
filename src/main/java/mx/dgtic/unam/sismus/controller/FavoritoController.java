package mx.dgtic.unam.sismus.controller;

import mx.dgtic.unam.sismus.dto.CancionResponseDto;
import mx.dgtic.unam.sismus.service.ListaService;
import mx.dgtic.unam.sismus.service.UsuarioCancionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/favoritos")
public class FavoritoController {

    private final UsuarioCancionService usuarioCancionService;
    private final ListaService listaService;

    public FavoritoController(UsuarioCancionService usuarioCancionService, ListaService listaService) {
        this.usuarioCancionService = usuarioCancionService;
        this.listaService = listaService;
    }

    @GetMapping("/top10")
    public String verTop10(Model model) {
        List<CancionResponseDto> top10 = usuarioCancionService.obtenerTop10CancionesMasDescargadas();
        String nickname = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("titulo", "Tus favoritos");
        model.addAttribute("canciones", top10);
        model.addAttribute("playlists", listaService.obtenerListasPorUsuario(nickname));
        model.addAttribute("contenido", "favoritos/listar");
        return "layout/main";
    }


}
