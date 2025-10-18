package mx.dgtic.unam.sismus.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistaDetalleDto {
    private Integer id;
    private String clave;
    private String nombre;
    private List<CancionResponseDto> canciones;
}
