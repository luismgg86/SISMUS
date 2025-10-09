package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.model.Cancion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CancionService {

    List<Cancion> listarTodas();

    Optional<Cancion> buscarPorId(Integer id);

    Cancion guardar(Cancion cancion);

    void eliminar(Integer id);

    boolean existsById(Integer id);

    Page<Cancion> buscarPorTituloPaginado(String titulo, Pageable pageable);
}
