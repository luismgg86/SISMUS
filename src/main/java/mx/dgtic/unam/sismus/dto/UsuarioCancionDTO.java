package mx.dgtic.unam.sismus.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UsuarioCancionDTO {
    private Integer id;
    private LocalDate fechaDescarga;
    private Integer cancionId;
    private String tituloCancion;
    private String artistaNombre;
}
