package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.dto.ArtistaDetalleDto;
import mx.dgtic.unam.sismus.dto.ArtistaDto;
import mx.dgtic.unam.sismus.exception.ArtistaNoEncontradoException;
import mx.dgtic.unam.sismus.exception.EntidadDuplicadaException;
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
    public List<ArtistaDto> listarActivos() {
        return artistaRepository.findByActivoTrue()
                .stream()
                .map(artistaMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ArtistaDto> listarInactivos() {
        return artistaRepository.findByActivoFalse()
                .stream()
                .map(artistaMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<ArtistaDto> buscarPorId(Integer id) {
        return artistaRepository.findById(id).map(artistaMapper::toDto);
    }

    public ArtistaDto guardar(ArtistaDto artistaDto) {
        if (artistaRepository.findByClave(artistaDto.getClave()).isPresent()) {
            throw new EntidadDuplicadaException("Ya existe un artista con la clave: " + artistaDto.getClave());
        }

        Artista artista = artistaMapper.toEntity(artistaDto);
        artista.setActivo(true);
        return artistaMapper.toDto(artistaRepository.save(artista));
    }

    public void actualizar(Integer id, ArtistaDto dto) {
        Artista existente = artistaRepository.findById(id)
                .orElseThrow(() -> new ArtistaNoEncontradoException("Artista no encontrado con ID " + id));

        Optional<Artista> otro = artistaRepository.findByClave(dto.getClave());
        if (otro.isPresent() && !otro.get().getArtistaId().equals(id)) {
            throw new EntidadDuplicadaException("Ya existe otro artista con la clave: " + dto.getClave());
        }

        existente.setNombre(dto.getNombre());
        existente.setClave(dto.getClave());
        artistaRepository.save(existente);
    }

    public void eliminar(Integer id) {
        Artista artista = artistaRepository.findById(id)
                .orElseThrow(() -> new ArtistaNoEncontradoException("Artista no encontrado con ID " + id));
        artista.setActivo(false);
        artistaRepository.save(artista);
    }

    public void reactivar(Integer id) {
        Artista artista = artistaRepository.findById(id)
                .orElseThrow(() -> new ArtistaNoEncontradoException("Artista no encontrado con ID " + id));
        artista.setActivo(true);
        artistaRepository.save(artista);
    }

    @Transactional(readOnly = true)
    public Optional<ArtistaDto> buscarPorClave(String clave) {
        return artistaRepository.findByClave(clave).map(artistaMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ArtistaDto> buscarPorNombre(String nombre) {
        return artistaRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(artistaMapper::toDto)
                .toList();
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
                .stream()
                .map(artistaMapper::toDetalleDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ArtistaDto> artistasSinCanciones() {
        return artistaRepository.artistasSinCanciones()
                .stream()
                .map(artistaMapper::toDto)
                .toList();
    }

    @Override
    public void activar(Integer id) {
        Artista artista = artistaRepository.findById(id)
                .orElseThrow(() -> new ArtistaNoEncontradoException("Artista con ID " + id + " no encontrado"));

        artista.setActivo(true);
        artistaRepository.save(artista);
    }

}
