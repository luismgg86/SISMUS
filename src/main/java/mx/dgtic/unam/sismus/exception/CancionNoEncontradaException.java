package mx.dgtic.unam.sismus.exception;

public class CancionNoEncontradaException extends RuntimeException {
    public CancionNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
