package mx.dgtic.unam.sismus.mapper;

import mx.dgtic.unam.sismus.dto.GeneroDto;
import mx.dgtic.unam.sismus.model.Genero;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class GeneroMapper {

    private final ModelMapper modelMapper;

    public GeneroMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public GeneroDto toDto(Genero genero) {
        return modelMapper.map(genero, GeneroDto.class);
    }

    public Genero toEntity(GeneroDto dto) {
        return modelMapper.map(dto, Genero.class);
    }
}
