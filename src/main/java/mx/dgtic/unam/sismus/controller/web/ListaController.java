package mx.dgtic.unam.sismus.controller.web;

import mx.dgtic.unam.sismus.dto.*;
import mx.dgtic.unam.sismus.exception.UsuarioNoEncontradoException;
import mx.dgtic.unam.sismus.service.CancionService;
import mx.dgtic.unam.sismus.service.ListaService;
import mx.dgtic.unam.sismus.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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
        ListaResponseDto playlist = listaService.obtenerConRelaciones(id);
        List<CancionResponseDto> cancionesDisponibles = cancionService.listarTodas();

        model.addAttribute("playlist", playlist);
        model.addAttribute("cancionesDisponibles", cancionesDisponibles);
        model.addAttribute("contenido", "playlist/detalle :: fragment");
        return "layout/main";
    }

    @GetMapping("/{id}/fragmento")
    public String obtenerFragmentoPlaylist(@PathVariable Integer id, Model model) {
        ListaResponseDto playlist = listaService.obtenerConRelaciones(id);
        List<CancionResponseDto> cancionesDisponibles = cancionService.listarTodas();

        model.addAttribute("playlist", playlist);
        model.addAttribute("cancionesDisponibles", cancionesDisponibles);
        return "playlist/detalle :: fragmentoTabla";
    }

    @PostMapping("/crear")
    public String crearPlaylist(@RequestParam String nombre,
                                @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            throw new RuntimeException("No hay usuario autenticado");
        }

        String nickname = userDetails.getUsername();

        var usuario = usuarioService.buscarPorNickname(nickname)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + nickname));

        ListaRequestDto dto = new ListaRequestDto();
        dto.setNombre(nombre);
        dto.setUsuarioId(usuario.getId());

        listaService.crearPlaylist(dto);

        return "redirect:/playlists";
    }



    //        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        Usuario usuario = usuarioService.buscarPorNickname(auth.getName())
//                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
//        dto.setUsuarioId(usuario.getId());
}
