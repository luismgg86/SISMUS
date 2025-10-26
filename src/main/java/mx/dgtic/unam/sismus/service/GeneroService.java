package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.dto.GeneroDto;

import java.util.List;
import java.util.Optional;

public interface GeneroService {
    List<GeneroDto> listarTodos();
    Optional<GeneroDto> buscarPorId(Integer id);
    GeneroDto guardar(GeneroDto genero);
    void eliminar(Integer id);
    void actualizar(Integer id, GeneroDto dto);
}
