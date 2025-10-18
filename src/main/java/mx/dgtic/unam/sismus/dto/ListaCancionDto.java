package mx.dgtic.unam.sismus.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListaCancionDto {
    private Integer id;
    private Integer listaId;
    private Integer cancionId;
    private LocalDate fechaAgregada;
}
