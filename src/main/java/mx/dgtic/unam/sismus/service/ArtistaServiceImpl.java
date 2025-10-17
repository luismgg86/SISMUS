package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.model.Artista;
import mx.dgtic.unam.sismus.repository.ArtistaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ArtistaServiceImpl implements ArtistaService {

    private final ArtistaRepository artistaRepository;

    public ArtistaServiceImpl(ArtistaRepository artistaRepository) {
        this.artistaRepository = artistaRepository;
    }

    @Override
    public List<Artista> listarTodos() {
        return artistaRepository.findAll();
    }

    @Override
    public Optional<Artista> buscarPorId(Integer id) {
        return artistaRepository.findById(id);
    }

    @Override
    @Transactional
    public Artista guardar(Artista artista) {
        return artistaRepository.save(artista);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        artistaRepository.deleteById(id);
    }

    //Busquedas
    @Override
    public Optional<Artista> buscarPorClave(String clave) {
        return artistaRepository.findByClave(clave);
    }

    @Override
    public List<Artista> buscarPorNombre(String nombre) {
        return artistaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public Page<Artista> buscarPorNombrePaginado(String nombre, Pageable pageable) {
        return artistaRepository.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    //Relaciones
    @Override
    public Optional<Artista> detalleConCancionesPorClave(String clave) {
        return artistaRepository.detalleConCancionesPorClave(clave);
    }

    @Override
    public List<Artista> listarConCanciones() {
        return artistaRepository.findAllConCanciones();
    }

    //Reportes
    @Override
    public List<Artista> artistasSinCanciones() {
        return artistaRepository.artistasSinCanciones();
    }

    @Override
    public List<Object[]> reporteArtistasPorTotalCanciones() {
        return artistaRepository.reporteArtistasPorTotalCanciones();
    }

    @Override
    public List<Object[]> reporteArtistasMasDescargados() {
        return artistaRepository.reporteArtistasMasDescargados();
    }

    //Consultas complejas
    @Override
    public List<Artista> buscarPorTituloDeCancion(String titulo) {
        return artistaRepository.buscarPorTituloDeCancion(titulo);
    }

    @Override
    public List<Artista> buscarPorGenero(String genero) {
        return artistaRepository.buscarPorGenero(genero);
    }
}
