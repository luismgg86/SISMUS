package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.dto.CancionRequestDto;
import mx.dgtic.unam.sismus.dto.CancionResponseDto;
import mx.dgtic.unam.sismus.exception.CancionNoEncontradaException;
import mx.dgtic.unam.sismus.mapper.CancionMapper;
import mx.dgtic.unam.sismus.model.Artista;
import mx.dgtic.unam.sismus.model.Cancion;
import mx.dgtic.unam.sismus.model.Genero;
import mx.dgtic.unam.sismus.repository.ArtistaRepository;
import mx.dgtic.unam.sismus.repository.CancionRepository;
import mx.dgtic.unam.sismus.repository.GeneroRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CancionServiceImpl implements CancionService {

    private final CancionRepository cancionRepository;
    private final ArtistaRepository artistaRepository;
    private final GeneroRepository generoRepository;
    private final CancionMapper cancionMapper;

    public CancionServiceImpl(CancionRepository cancionRepository,
                              ArtistaRepository artistaRepository,
                              GeneroRepository generoRepository,
                              CancionMapper cancionMapper) {
        this.cancionRepository = cancionRepository;
        this.artistaRepository = artistaRepository;
        this.generoRepository = generoRepository;
        this.cancionMapper = cancionMapper;
    }

    @Transactional(readOnly = true)
    public List<CancionResponseDto> listarTodas() {
        return cancionRepository.findAll()
                .stream()
                .map(cancionMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<CancionResponseDto> buscarPorId(Integer id) {
        return cancionRepository.findById(id).map(cancionMapper::toResponseDto);
    }

    public CancionResponseDto guardar(CancionRequestDto dto) {
        Artista artista = artistaRepository.findById(dto.getArtistaId())
                .orElseThrow(() -> new IllegalArgumentException("Artista no encontrado"));
        Genero genero = generoRepository.findById(dto.getGeneroId())
                .orElseThrow(() -> new IllegalArgumentException("Género no encontrado"));
        Cancion cancion = cancionMapper.toEntity(dto, artista, genero);
        return cancionMapper.toResponseDto(cancionRepository.save(cancion));
    }

    // En CancionServiceImpl
    @Override
    public void guardarCancionConArchivo(CancionRequestDto dto, MultipartFile archivo) throws IOException {

        Artista artista = artistaRepository.findById(dto.getArtistaId())
                .orElseThrow(() -> new RuntimeException("Artista no encontrado"));

        Genero genero = generoRepository.findById(dto.getGeneroId())
                .orElseThrow(() -> new RuntimeException("Género no encontrado"));

        String nombreArchivo = archivo.getOriginalFilename();
        Path ruta = Paths.get("src/main/resources/static/audios/" + nombreArchivo);
        Files.createDirectories(ruta.getParent());
        Files.write(ruta, archivo.getBytes());

        dto.setAudio("/audios/" + nombreArchivo);
        dto.setFechaAlta(LocalDate.now());

        Cancion entidad = cancionMapper.toEntity(dto, artista, genero);

        cancionRepository.save(entidad);
    }




    public void eliminar(Integer id) {
        if (!cancionRepository.existsById(id))
            throw new CancionNoEncontradaException("No existe canción con ID: " + id);
        cancionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<CancionResponseDto> buscarPorTituloPaginado(String titulo, Pageable pageable) {
        Page<Cancion> page = (titulo == null || titulo.isEmpty())
                ? cancionRepository.findAll(pageable)
                : cancionRepository.findByTituloContainingIgnoreCase(titulo, pageable);
        return page.map(cancionMapper::toResponseDto);
    }
}
