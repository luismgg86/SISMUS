package mx.dgtic.unam.sismus.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ManejadorGlobalExcepciones {

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public String manejarUsuarioNoEncontrado(UsuarioNoEncontradoException ex, Model modelo) {
        modelo.addAttribute("codigo", 404);
        modelo.addAttribute("titulo", "Usuario no encontrado");
        modelo.addAttribute("mensaje", ex.getMessage());
        return "error/error-404";
    }

    @ExceptionHandler({CancionNoEncontradaException.class, ListaNoEncontradaException.class})
    public String manejarEntidadNoEncontrada(RuntimeException ex, Model modelo) {
        modelo.addAttribute("codigo", 404);
        modelo.addAttribute("titulo", "Recurso no encontrado");
        modelo.addAttribute("mensaje", ex.getMessage());
        return "error/error-404";
    }

    @ExceptionHandler(EntidadDuplicadaException.class)
    public String manejarDuplicados(EntidadDuplicadaException ex, Model modelo) {
        modelo.addAttribute("codigo", 409);
        modelo.addAttribute("titulo", "Conflicto de datos");
        modelo.addAttribute("mensaje", ex.getMessage());
        return "error/error-duplicado";
    }

    @ExceptionHandler(Exception.class)
    public String manejarErrorGeneral(Exception ex, Model modelo) {
        modelo.addAttribute("codigo", 500);
        modelo.addAttribute("titulo", "Error interno del servidor");
        modelo.addAttribute("mensaje", "Ocurrió un error inesperado.");
        modelo.addAttribute("detalle", ex.getMessage());
        return "error/error";
    }
}
