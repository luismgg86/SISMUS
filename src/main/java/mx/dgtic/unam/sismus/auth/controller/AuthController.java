package mx.dgtic.unam.sismus.auth.controller;

import mx.dgtic.unam.sismus.dto.UsuarioRegistroDto;
import mx.dgtic.unam.sismus.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
}

