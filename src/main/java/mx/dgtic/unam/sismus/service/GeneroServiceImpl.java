package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.model.Genero;
import mx.dgtic.unam.sismus.repository.GeneroRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GeneroServiceImpl implements GeneroService {

    private final GeneroRepository generoRepository;

    public GeneroServiceImpl(GeneroRepository generoRepository) {
        this.generoRepository = generoRepository;
    }

    @Override
    public List<Genero> listarTodos() {
        return generoRepository.findAllByOrderByNombreAsc();
    }

    @Override
    public Optional<Genero> buscarPorId(Integer id) {
        return generoRepository.findById(id);
    }

    @Override
    @Transactional
    public Genero guardar(Genero genero) {
        return generoRepository.save(genero);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        generoRepository.deleteById(id);
    }

    //Busquedas
    @Override
    public Optional<Genero> buscarPorClave(String clave) {
        return generoRepository.findByClave(clave);
    }

    @Override
    public List<Genero> buscarPorNombre(String nombre) {
        return generoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public Page<Genero> buscarPorNombrePaginado(String nombre, Pageable pageable) {
        return generoRepository.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    //Relaciones
    @Override
    public Optional<Genero> detalleConCancionesPorClave(String clave) {
        return generoRepository.detalleConCancionesPorClave(clave);
    }

    //Reportes
    @Override
    public List<Genero> generosSinCanciones() {
        return generoRepository.generosSinCanciones();
    }

    @Override
    public List<Object[]> reporteConteoCancionesPorGenero() {
        return generoRepository.reporteConteoCancionesPorGenero();
    }

    //Consultas complejas
    @Override
    public List<Genero> buscarPorTituloDeCancion(String titulo) {
        return generoRepository.buscarPorTituloDeCancion(titulo);
    }
}
