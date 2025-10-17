package mx.dgtic.unam.sismus.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ListaCancionDTO {
    private Integer id;
    private LocalDate fechaAgregada;
    private CancionDTO cancion;
}
