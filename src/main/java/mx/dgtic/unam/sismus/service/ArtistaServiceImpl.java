package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.dto.ArtistaDetalleDto;
import mx.dgtic.unam.sismus.dto.ArtistaDto;
import mx.dgtic.unam.sismus.exception.ArtistaNoEncontradoException;
import mx.dgtic.unam.sismus.mapper.ArtistaMapper;
import mx.dgtic.unam.sismus.model.Artista;
import mx.dgtic.unam.sismus.repository.ArtistaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ArtistaServiceImpl implements ArtistaService {

    private final ArtistaRepository artistaRepository;
    private final ArtistaMapper artistaMapper;

    public ArtistaServiceImpl(ArtistaRepository artistaRepository, ArtistaMapper artistaMapper) {
        this.artistaRepository = artistaRepository;
        this.artistaMapper = artistaMapper;
    }

    @Transactional(readOnly = true)
    public List<ArtistaDto> listarTodos() {
        return artistaRepository.findAll()
                .stream()
                .map(artistaMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<ArtistaDto> buscarPorId(Integer id) {
        return artistaRepository.findById(id).map(artistaMapper::toDto);
    }

    public ArtistaDto guardar(ArtistaDto artistaDto) {
        Artista artista = artistaMapper.toEntity(artistaDto);
        return artistaMapper.toDto(artistaRepository.save(artista));
    }

    public void eliminar(Integer id) {
        if (!artistaRepository.existsById(id))
            throw new ArtistaNoEncontradoException("Artista no encontrado con ID " + id);
        artistaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<ArtistaDto> buscarPorClave(String clave) {
        return artistaRepository.findByClave(clave).map(artistaMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ArtistaDto> buscarPorNombre(String nombre) {
        return artistaRepository.findByNombreContainingIgnoreCase(nombre)
                .stream().map(artistaMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Page<ArtistaDto> buscarPorNombrePaginado(String nombre, Pageable pageable) {
        return artistaRepository.findByNombreContainingIgnoreCase(nombre, pageable)
                .map(artistaMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ArtistaDetalleDto> detalleConCancionesPorClave(String clave) {
        return artistaRepository.detalleConCancionesPorClave(clave)
                .map(artistaMapper::toDetalleDto);
    }

    @Transactional(readOnly = true)
    public List<ArtistaDetalleDto> listarConCanciones() {
        return artistaRepository.findAllConCanciones()
                .stream().map(artistaMapper::toDetalleDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ArtistaDto> artistasSinCanciones() {
        return artistaRepository.artistasSinCanciones()
                .stream().map(artistaMapper::toDto).toList();
    }
}
