package mx.dgtic.unam.sismus.mapper;

import mx.dgtic.unam.sismus.dto.ArtistaDetalleDto;
import mx.dgtic.unam.sismus.dto.ArtistaDto;
import mx.dgtic.unam.sismus.dto.CancionResponseDto;
import mx.dgtic.unam.sismus.model.Artista;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper para conversión entre entidades Artista y sus DTOs.
 */
@Component
public class ArtistaMapper {

    private final ModelMapper modelMapper;

    public ArtistaMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ArtistaDto toDto(Artista entity) {
        return modelMapper.map(entity, ArtistaDto.class);
    }

    public Artista toEntity(ArtistaDto dto) {
        return modelMapper.map(dto, Artista.class);
    }

    public ArtistaDetalleDto toDetalleDto(Artista entity) {
        ArtistaDetalleDto dto = modelMapper.map(entity, ArtistaDetalleDto.class);
        if (entity.getCanciones() != null) {
            dto.setCanciones(entity.getCanciones().stream()
                    .map(c -> modelMapper.map(c, CancionResponseDto.class))
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
