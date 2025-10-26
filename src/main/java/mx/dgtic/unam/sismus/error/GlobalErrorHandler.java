package mx.dgtic.unam.sismus.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice(basePackages = "mx.dgtic.unam.sismus.controller.web")
public class GlobalErrorHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handleNotFound(NoHandlerFoundException ex, WebRequest request, Model model) {
        if (isAjaxRequest(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recurso no encontrado");
        }
        model.addAttribute("errorCode", 404);
        model.addAttribute("errorMessage", "La página solicitada no existe.");
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public Object handleInternalError(Exception ex, WebRequest request, Model model) {
        if (isAjaxRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno: " + ex.getMessage());
        }

        model.addAttribute("errorCode", 500);
        model.addAttribute("errorMessage", "Ocurrió un error interno en el servidor.");
        return "error/500";
    }

    private boolean isAjaxRequest(WebRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        return requestedWith != null && requestedWith.equalsIgnoreCase("XMLHttpRequest");
    }
}
