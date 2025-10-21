package mx.dgtic.unam.sismus.controller.web;

import mx.dgtic.unam.sismus.dto.UsuarioResponseDto;
import mx.dgtic.unam.sismus.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    public AdminUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listarUsuarios(Model model) {
        List<UsuarioResponseDto> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("contenido", "admin/usuarios");
        return "layout/main";
    }

    @GetMapping("/activar/{id}")
    public String activar(@PathVariable Integer id) {
        usuarioService.cambiarEstado(id, true);
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Integer id) {
        usuarioService.cambiarEstado(id, false);
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/roles/{id}")
    public String editarRoles(@PathVariable Integer id, Model model) {
        UsuarioResponseDto usuario = usuarioService.listarTodos()
                .stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("usuario", usuario);
        model.addAttribute("contenido", "admin/usuario-roles");
        return "layout/main";
    }

    @PostMapping("/roles/guardar")
    public String guardarRoles(@RequestParam Integer id, @RequestParam Set<String> rolesSeleccionados) {
        usuarioService.actualizarRoles(id, rolesSeleccionados);
        return "redirect:/admin/usuarios";
    }
}

