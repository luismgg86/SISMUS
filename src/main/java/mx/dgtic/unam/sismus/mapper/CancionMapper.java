package mx.dgtic.unam.sismus.mapper;

import mx.dgtic.unam.sismus.dto.CancionRequestDto;
import mx.dgtic.unam.sismus.dto.CancionResponseDto;
import mx.dgtic.unam.sismus.model.Artista;
import mx.dgtic.unam.sismus.model.Cancion;
import mx.dgtic.unam.sismus.model.Genero;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CancionMapper {

    private final ModelMapper modelMapper;

    public CancionMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public CancionResponseDto toResponseDto(Cancion cancion) {
        CancionResponseDto dto = modelMapper.map(cancion, CancionResponseDto.class);
        dto.setArtistaNombre(cancion.getArtista().getNombre());
        dto.setGeneroNombre(cancion.getGenero().getNombre());
        return dto;
    }

    public Cancion toEntity(CancionRequestDto dto, Artista artista, Genero genero) {
        Cancion entity = modelMapper.map(dto, Cancion.class);
        entity.setArtista(artista);
        entity.setGenero(genero);
        return entity;
    }
}
