package mx.dgtic.unam.sismus.auth.controller;

import mx.dgtic.unam.sismus.dto.UsuarioRegistroDto;
import mx.dgtic.unam.sismus.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    @GetMapping("/login")
    public String mostrarLogin() {
        return "auth/login"; // Ruta corresponda a templates/auth/login.html
    }

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/register")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new UsuarioRegistroDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registrarUsuario(@ModelAttribute("usuario") UsuarioRegistroDto dto, Model model) {
        try {
            usuarioService.registrar(dto);
            return "redirect:/login?success";
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo registrar: " + e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/recuperar-password")
    public String mostrarFormularioRecuperarPassword() {
        return "auth/recuperar-password";
    }

    @PostMapping("/recuperar-password")
    public String procesarRecuperacion(@RequestParam String correo,
                                       @RequestParam String nuevaPassword,
                                       @RequestParam String confirmarPassword,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {

        if (!nuevaPassword.equals(confirmarPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            model.addAttribute("correo", correo);
            return "auth/recuperar-password";
        }

        var usuario = usuarioService.buscarUsuarioPorCorreo(correo);
        if (usuario.isEmpty()) {
            model.addAttribute("error", "No existe un usuario con ese correo.");
            return "auth/recuperar-password";
        }

        usuarioService.actualizarPassword(usuario.get().getId(), nuevaPassword);

        redirectAttributes.addFlashAttribute("success", "Contraseña actualizada. Inicia sesión.");
        return "redirect:/login";
    }



}

