package mx.dgtic.unam.sismus.mapper;

import mx.dgtic.unam.sismus.dto.UsuarioCancionDto;
import mx.dgtic.unam.sismus.model.UsuarioCancion;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversión entre UsuarioCancion y su DTO.
 */
@Component
public class UsuarioCancionMapper {

    private final ModelMapper modelMapper;

    public UsuarioCancionMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public UsuarioCancionDto toDto(UsuarioCancion entity) {
        return modelMapper.map(entity, UsuarioCancionDto.class);
    }

    public UsuarioCancion toEntity(UsuarioCancionDto dto) {
        return modelMapper.map(dto, UsuarioCancion.class);
    }
}
