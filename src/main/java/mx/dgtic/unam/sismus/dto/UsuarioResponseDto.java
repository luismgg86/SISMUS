package mx.dgtic.unam.sismus.dto;

import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDto {
    private Integer id;
    private String nombreCompleto;
    private String correo;
    private String nickname;
    private Set<String> roles;
}

