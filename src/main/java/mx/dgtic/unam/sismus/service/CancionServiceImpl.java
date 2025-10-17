package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.exception.CancionNoEncontradaException;
import mx.dgtic.unam.sismus.model.Cancion;
import mx.dgtic.unam.sismus.repository.CancionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CancionServiceImpl implements CancionService {

    private final CancionRepository cancionRepository;

    public CancionServiceImpl(CancionRepository cancionRepository) {
        this.cancionRepository = cancionRepository;
    }

    @Override
    public List<Cancion> listarTodas() {
        return cancionRepository.findAll();
    }

    @Override
    public Optional<Cancion> buscarPorId(Integer id) {
        return cancionRepository.findById(id);
    }

    @Override
    public Cancion guardar(Cancion cancion) {
        return cancionRepository.save(cancion);
    }

    @Override
    public void eliminar(Integer id) {
        if (!cancionRepository.existsById(id)) {
            throw new CancionNoEncontradaException("La canción con ID " + id + " no existe.");
        }
        cancionRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return cancionRepository.existsById(id);
    }

    @Override
    public Page<Cancion> buscarPorTituloPaginado(String titulo, Pageable pageable) {
        if (titulo == null || titulo.isEmpty()) {
            return cancionRepository.findAll(pageable);
        }
        return cancionRepository.findByTituloContainingIgnoreCase(titulo, pageable);
    }
}
