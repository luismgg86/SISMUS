package mx.dgtic.unam.sismus.controller.web;

import mx.dgtic.unam.sismus.model.Lista;
import mx.dgtic.unam.sismus.service.CancionService;
import mx.dgtic.unam.sismus.service.ListaService;
import mx.dgtic.unam.sismus.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/playlists")
public class ListaController {

    private final ListaService listaService;
    private final CancionService cancionService;
    private final UsuarioService usuarioService;

    public ListaController(ListaService listaService, CancionService cancionService, UsuarioService usuarioService) {
        this.listaService = listaService;
        this.cancionService = cancionService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{id}")
    public String verPlaylist(@PathVariable Integer id, Model model) {
        Lista playlist = listaService.obtenerConRelaciones(id);
        model.addAttribute("playlist", playlist);
        model.addAttribute("cancionesDisponibles", cancionService.listarTodas());
        model.addAttribute("contenido", "playlist/detalle :: fragment");
        return "layout/main";
    }

    @GetMapping("/{id}/fragmento")
    public String obtenerFragmentoPlaylist(@PathVariable Integer id, Model model) {
        Lista playlist = listaService.obtenerConRelaciones(id);
        model.addAttribute("playlist", playlist);
        model.addAttribute("cancionesDisponibles", cancionService.listarTodas());
        return "playlist/detalle :: fragmentoTabla";
    }
}

