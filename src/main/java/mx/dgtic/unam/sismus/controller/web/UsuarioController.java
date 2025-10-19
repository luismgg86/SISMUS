package mx.dgtic.unam.sismus.controller.web;

import mx.dgtic.unam.sismus.dto.UsuarioResponseDto;
import mx.dgtic.unam.sismus.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            String nickname = userDetails.getUsername();
            UsuarioResponseDto usuario = usuarioService.buscarPorNickname(nickname)
                    .orElse(null);
            model.addAttribute("usuario", usuario);
        }
        model.addAttribute("contenido", "usuario/perfil :: fragment");
        return "layout/main";
    }
}
