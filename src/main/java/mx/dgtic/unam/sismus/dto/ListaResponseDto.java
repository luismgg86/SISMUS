package mx.dgtic.unam.sismus.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListaResponseDto {
    private Integer id;
    private String nombre;
    private LocalDate fechaCreacion;
    private String usuarioNickname;
    private Integer totalCancionesActivas;
    private List<CancionResponseDto> canciones;
}
