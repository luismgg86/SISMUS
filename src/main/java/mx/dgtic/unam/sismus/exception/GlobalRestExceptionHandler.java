package mx.dgtic.unam.sismus.exception;

import mx.dgtic.unam.sismus.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "mx.dgtic.unam.sismus.controller.api")
public class GlobalRestExceptionHandler {

    @ExceptionHandler(CancionDuplicadaException.class)
    public ResponseEntity<String> handleCancionDuplicada(CancionDuplicadaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(CancionNoEncontradaException.class)
    public ResponseEntity<String> handleCancionNoEncontrada(CancionNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ListaNoEncontradaException.class)
    public ResponseEntity<String> handleListaNoEncontrada(ListaNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralError(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno: " + ex.getMessage());
    }
}
