package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.dto.GeneroDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface GeneroService {

    List<GeneroDto> listarTodos();
    Optional<GeneroDto> buscarPorId(Integer id);
    GeneroDto guardar(GeneroDto genero);
    void eliminar(Integer id);

    Optional<GeneroDto> buscarPorClave(String clave);
    List<GeneroDto> buscarPorNombre(String nombre);
    Page<GeneroDto> buscarPorNombrePaginado(String nombre, Pageable pageable);
}
