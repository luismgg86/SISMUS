package mx.dgtic.unam.sismus.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackages = "mx.dgtic.unam.sismus.controller.web")
public class GlobalExceptionHandler {

    @ExceptionHandler({
            UsuarioNoEncontradoException.class,
            CancionNoEncontradaException.class,
            ListaNoEncontradaException.class,
            GeneroNoEncontradoException.class,
            ArtistaNoEncontradoException.class,
            RolNoEncontradoException.class,
            DescargaNoEncontradaException.class
    })
    public String manejarRecursoNoEncontrado(RuntimeException ex, Model modelo) {
        modelo.addAttribute("codigo", 404);
        modelo.addAttribute("titulo", "Recurso no encontrado");
        modelo.addAttribute("mensaje", ex.getMessage());
        return "error/error-404";
    }

    @ExceptionHandler(EntidadDuplicadaException.class)
    public String manejarDuplicado(EntidadDuplicadaException ex, Model modelo) {
        modelo.addAttribute("codigo", 409);
        modelo.addAttribute("titulo", "Conflicto de datos");
        modelo.addAttribute("mensaje", ex.getMessage());
        return "error/error-duplicado";
    }

    @ExceptionHandler(OperacionNoPermitidaException.class)
    public String manejarOperacionNoPermitida(OperacionNoPermitidaException ex, Model modelo) {
        modelo.addAttribute("codigo", 403);
        modelo.addAttribute("titulo", "Operación no permitida");
        modelo.addAttribute("mensaje", ex.getMessage());
        return "error/error";
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
