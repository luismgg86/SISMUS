package mx.dgtic.unam.sismus.dto;

import lombok.Data;

@Data
public class CancionDTO {
    private Integer id;
    private String titulo;
    private String artista;
    private String genero;
    private Integer duracion;
}
