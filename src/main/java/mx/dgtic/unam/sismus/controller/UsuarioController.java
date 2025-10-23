package mx.dgtic.unam.sismus.controller;

import mx.dgtic.unam.sismus.dto.CambiarPasswordDto;
import mx.dgtic.unam.sismus.dto.UsuarioResponseDto;
import mx.dgtic.unam.sismus.exception.UsuarioNoEncontradoException;
import mx.dgtic.unam.sismus.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            String nickname = userDetails.getUsername();
            UsuarioResponseDto usuario = usuarioService.buscarPorNickname(nickname)
                    .orElse(null);
            model.addAttribute("usuario", usuario);
        }
        model.addAttribute("contenido", "usuario/perfil");
        return "layout/main";
    }

    @GetMapping("/cambiar-password")
    public String mostrarFormularioCambioPassword(Model model) {
        model.addAttribute("passwordForm", new CambiarPasswordDto());
        model.addAttribute("contenido", "usuario/cambiar-password");
        return "layout/main";
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(@AuthenticationPrincipal UserDetails userDetails,
                                  @ModelAttribute("passwordForm") CambiarPasswordDto form,
                                  RedirectAttributes redirectAttributes) {

        var usuario = usuarioService.buscarUsuarioPorNickname(userDetails.getUsername())
                .orElseThrow(() -> new UsuarioNoEncontradoException("No se encontró el usuario"));

        if (!passwordEncoder.matches(form.getPasswordActual(), usuario.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "La contraseña actual es incorrecta.");
            return "redirect:/usuario/cambiar-password";
        }

        if (!form.getNuevaPassword().equals(form.getConfirmarPassword())) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas nuevas no coinciden.");
            return "redirect:/usuario/cambiar-password";
        }

        usuarioService.actualizarPassword(usuario.getId(), form.getNuevaPassword());

        redirectAttributes.addFlashAttribute("mensajeExito", "Contraseña actualizada correctamente.");
        return "redirect:/usuario/perfil";
    }
}
