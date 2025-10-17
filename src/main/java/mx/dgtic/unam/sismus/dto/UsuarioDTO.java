package mx.dgtic.unam.sismus.dto;

import lombok.Data;
import java.util.List;

@Data
public class UsuarioDTO {
    private Integer id;
    private String nombre;
    private String nickname;
    private List<ListaDTO> listas;
}
