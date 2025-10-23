package mx.dgtic.unam.sismus.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancionResponseDto {
    private Integer id;
    private String titulo;
    private String audio;
    private Integer duracion;
    private Boolean activo;
    private LocalDate fechaAlta;
    private String artistaNombre;
    private String generoNombre;
    private LocalDate fechaAgregada;
}
