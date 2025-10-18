package mx.dgtic.unam.sismus.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListaRequestDto {
    private String nombre;
    private Integer usuarioId;
}
