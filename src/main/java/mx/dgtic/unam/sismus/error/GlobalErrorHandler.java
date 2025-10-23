package mx.dgtic.unam.sismus.error;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(NoHandlerFoundException ex, Model model) {
        model.addAttribute("errorCode", 404);
        model.addAttribute("errorMessage", "La página solicitada no existe.");
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleInternalError(Exception ex, Model model) {
        model.addAttribute("errorCode", 500);
        model.addAttribute("errorMessage", "Ocurrió un error interno en el servidor.");
        return "error/500";
    }
}
