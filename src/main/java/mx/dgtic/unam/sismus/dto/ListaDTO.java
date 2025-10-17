package mx.dgtic.unam.sismus.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ListaDTO {
    private Integer id;
    private String nombre;
    private LocalDate fechaCreacion;
    private String usuarioNickname;
    private List<ListaCancionDTO> canciones;
}
