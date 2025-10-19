package mx.dgtic.unam.sismus.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRegistroDto {
    private String nombre;
    private String apPaterno;
    private String apMaterno;
    private String correo;
    private String nickname;
    private String password;
}
