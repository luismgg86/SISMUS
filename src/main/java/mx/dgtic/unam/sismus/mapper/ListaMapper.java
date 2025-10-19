package mx.dgtic.unam.sismus.mapper;

import mx.dgtic.unam.sismus.dto.CancionResponseDto;
import mx.dgtic.unam.sismus.dto.ListaRequestDto;
import mx.dgtic.unam.sismus.dto.ListaResponseDto;
import mx.dgtic.unam.sismus.model.Cancion;
import mx.dgtic.unam.sismus.model.Lista;
import mx.dgtic.unam.sismus.model.Usuario;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ListaMapper {

    private final ModelMapper modelMapper;

    public ListaMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Lista toEntity(ListaRequestDto dto, Usuario usuario) {
        Lista lista = modelMapper.map(dto, Lista.class);
        lista.setUsuario(usuario);
        lista.setId(null);
        return lista;
    }

    public ListaResponseDto toResponseDto(Lista lista) {
        return modelMapper.map(lista, ListaResponseDto.class);
    }

    public CancionResponseDto mapCancion(Cancion cancion) {
        return modelMapper.map(cancion, CancionResponseDto.class);
    }
}

