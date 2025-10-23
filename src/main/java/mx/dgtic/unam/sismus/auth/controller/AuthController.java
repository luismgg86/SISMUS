package mx.dgtic.unam.sismus.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
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

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String success,
            @RequestParam(required = false) String logout,
            HttpServletRequest request,
            Model model) {

        // Error de autenticación
        Object errorMessage = request.getSession().getAttribute("login_error");
        if (errorMessage != null) {
            model.addAttribute("login_error", errorMessage);
            request.getSession().removeAttribute("login_error");
        }

        // Mensaje de registro exitoso
        if (success != null) {
            model.addAttribute("login_success", "Cuenta creada correctamente. Inicia sesión.");
        }

        // Mensaje de logout
        if (logout != null) {
            model.addAttribute("logout_message", "Sesión cerrada correctamente.");
        }

        return "auth/login";
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
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuario", dto);
            return "auth/register";
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo registrar: " + e.getMessage());
            model.addAttribute("usuario", dto);
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
        return "redirect:/login?success=true";
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "error/403";
    }
}
