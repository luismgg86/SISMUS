package mx.dgtic.unam.sismus.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "mx.dgtic.unam.sismus.controller.api")
public class GlobalRestExceptionHandler {

    private ResponseEntity<Object> buildResponse(HttpStatus status, String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", mensaje);
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler({
            UsuarioNoEncontradoException.class,
            CancionNoEncontradaException.class,
            ListaNoEncontradaException.class,
            GeneroNoEncontradoException.class,
            ArtistaNoEncontradoException.class,
            RolNoEncontradoException.class,
            DescargaNoEncontradaException.class
    })
    public ResponseEntity<Object> manejarNoEncontrado(RuntimeException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(EntidadDuplicadaException.class)
    public ResponseEntity<Object> manejarDuplicado(EntidadDuplicadaException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(OperacionNoPermitidaException.class)
    public ResponseEntity<Object> manejarOperacionNoPermitida(OperacionNoPermitidaException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> manejarArgumentoInvalido(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> manejarErrorGeneral(Exception ex) {
        ex.printStackTrace();
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }
}
