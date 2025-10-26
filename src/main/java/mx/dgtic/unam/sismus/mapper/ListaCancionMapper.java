package mx.dgtic.unam.sismus.mapper;

import mx.dgtic.unam.sismus.dto.ListaCancionDto;
import mx.dgtic.unam.sismus.model.ListaCancion;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversión entre ListaCancion y su DTO.
 */
@Component
public class ListaCancionMapper {

    private final ModelMapper modelMapper;

    public ListaCancionMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ListaCancionDto toDto(ListaCancion entity) {
        ListaCancionDto dto = modelMapper.map(entity, ListaCancionDto.class);
        dto.setListaId(entity.getLista().getId());
        dto.setCancionId(entity.getCancion().getId());
        return dto;
    }

    public ListaCancion toEntity(ListaCancionDto dto) {
        return modelMapper.map(dto, ListaCancion.class);
    }
}
