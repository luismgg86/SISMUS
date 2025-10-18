package mx.dgtic.unam.sismus.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancionRequestDto {
    private String titulo;
    private String audio;
    private Integer duracion;
    private LocalDate fechaAlta;
    private Integer artistaId;
    private Integer generoId;
}
