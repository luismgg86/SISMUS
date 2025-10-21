package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.dto.GeneroDto;
import mx.dgtic.unam.sismus.exception.EntidadDuplicadaException;
import mx.dgtic.unam.sismus.exception.GeneroNoEncontradoException;
import mx.dgtic.unam.sismus.mapper.GeneroMapper;
import mx.dgtic.unam.sismus.model.Genero;
import mx.dgtic.unam.sismus.repository.GeneroRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GeneroServiceImpl implements GeneroService {

    private final GeneroRepository generoRepository;
    private final GeneroMapper generoMapper;

    public GeneroServiceImpl(GeneroRepository generoRepository, GeneroMapper generoMapper) {
        this.generoRepository = generoRepository;
        this.generoMapper = generoMapper;
    }

    @Transactional(readOnly = true)
    public List<GeneroDto> listarTodos() {
        return generoRepository.findAllByOrderByNombreAsc()
                .stream()
                .map(generoMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<GeneroDto> buscarPorId(Integer id) {
        return generoRepository.findById(id).map(generoMapper::toDto);
    }

    public GeneroDto guardar(GeneroDto generoDto) {
        Genero genero = generoMapper.toEntity(generoDto);
        return generoMapper.toDto(generoRepository.save(genero));
    }

    public void eliminar(Integer id) {
        if (!generoRepository.existsById(id))
            throw new GeneroNoEncontradoException("Género no encontrado con ID " + id);
        generoRepository.deleteById(id);
    }

    @Override
    public void actualizar(Integer id, GeneroDto dto) {
        Genero existente = generoRepository.findById(id)
                .orElseThrow(() -> new GeneroNoEncontradoException("Género con ID " + id + " no encontrado"));

        if (!existente.getClave().equals(dto.getClave()) &&
                generoRepository.existsByClave(dto.getClave())) {
            throw new EntidadDuplicadaException("Ya existe un género con la clave: " + dto.getClave());
        }

        existente.setClave(dto.getClave());
        existente.setNombre(dto.getNombre());
        generoRepository.save(existente);
    }

    @Transactional(readOnly = true)
    public Optional<GeneroDto> buscarPorClave(String clave) {
        return generoRepository.findByClave(clave).map(generoMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<GeneroDto> buscarPorNombre(String nombre) {
        return generoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream().map(generoMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Page<GeneroDto> buscarPorNombrePaginado(String nombre, Pageable pageable) {
        return generoRepository.findByNombreContainingIgnoreCase(nombre, pageable)
                .map(generoMapper::toDto);
    }


}
