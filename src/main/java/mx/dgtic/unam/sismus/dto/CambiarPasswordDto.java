package mx.dgtic.unam.sismus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambiarPasswordDto {
    private String passwordActual;
    private String nuevaPassword;
    private String confirmarPassword;
}

