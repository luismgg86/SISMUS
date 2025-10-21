package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.dto.CancionRequestDto;
import mx.dgtic.unam.sismus.dto.CancionResponseDto;
import mx.dgtic.unam.sismus.exception.ArtistaNoEncontradoException;
import mx.dgtic.unam.sismus.exception.CancionNoEncontradaException;
import mx.dgtic.unam.sismus.exception.GeneroNoEncontradoException;
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

    // ✅ Listar solo canciones activas
    @Transactional(readOnly = true)
    public List<CancionResponseDto> listarTodas() {
        return cancionRepository.findByActivoTrue()
                .stream()
                .map(cancionMapper::toResponseDto)
                .toList();
    }

    // ✅ Buscar por ID (solo si está activa)
    @Transactional(readOnly = true)
    public Optional<CancionResponseDto> buscarPorId(Integer id) {
        return cancionRepository.findById(id)
                .filter(Cancion::getActivo)
                .map(cancionMapper::toResponseDto);
    }

    // ✅ Guardar nueva canción
    public CancionResponseDto guardar(CancionRequestDto dto) {
        Artista artista = artistaRepository.findById(dto.getArtistaId())
                .orElseThrow(() -> new ArtistaNoEncontradoException("No existe artista con id: " + dto.getArtistaId()));
        Genero genero = generoRepository.findById(dto.getGeneroId())
                .orElseThrow(() -> new GeneroNoEncontradoException("No existe género con id: " + dto.getGeneroId()));
        Cancion cancion = cancionMapper.toEntity(dto, artista, genero);
        cancion.setActivo(true);
        return cancionMapper.toResponseDto(cancionRepository.save(cancion));
    }

    // ✅ Actualizar canción (mantiene activa)
    @Override
    public void actualizarCancion(Integer id, CancionRequestDto dto, MultipartFile archivo) throws IOException {
        Cancion cancion = cancionRepository.findById(id)
                .orElseThrow(() -> new CancionNoEncontradaException("No existe canción con id: " + id));

        Artista artista = artistaRepository.findById(dto.getArtistaId())
                .orElseThrow(() -> new ArtistaNoEncontradoException("No existe artista con id: " + dto.getArtistaId()));
        Genero genero = generoRepository.findById(dto.getGeneroId())
                .orElseThrow(() -> new GeneroNoEncontradoException("No existe género con id: " + dto.getGeneroId()));

        cancion.setTitulo(dto.getTitulo());
        cancion.setDuracion(dto.getDuracion());
        cancion.setArtista(artista);
        cancion.setGenero(genero);
        cancion.setFechaAlta(LocalDate.now());
        cancion.setActivo(true);

        if (archivo != null && !archivo.isEmpty()) {
            String nombreArchivo = archivo.getOriginalFilename();
            Path ruta = Paths.get("src/main/resources/static/audios/" + nombreArchivo);
            Files.createDirectories(ruta.getParent());
            Files.write(ruta, archivo.getBytes());
            cancion.setAudio("/audios/" + nombreArchivo);
        }

        cancionRepository.save(cancion);
    }

    // ✅ Guardar canción con archivo (nueva)
    @Override
    public void guardarCancionConArchivo(CancionRequestDto dto, MultipartFile archivo) throws IOException {

        Artista artista = artistaRepository.findById(dto.getArtistaId())
                .orElseThrow(() -> new ArtistaNoEncontradoException("No existe artista con id: " + dto.getArtistaId()));

        Genero genero = generoRepository.findById(dto.getGeneroId())
                .orElseThrow(() -> new GeneroNoEncontradoException("No existe género con id: " + dto.getGeneroId()));

        String nombreArchivo = archivo.getOriginalFilename();
        Path ruta = Paths.get("src/main/resources/static/audios/" + nombreArchivo);
        Files.createDirectories(ruta.getParent());
        Files.write(ruta, archivo.getBytes());

        dto.setAudio("/audios/" + nombreArchivo);
        dto.setFechaAlta(LocalDate.now());

        Cancion entidad = cancionMapper.toEntity(dto, artista, genero);
        entidad.setActivo(true);

        cancionRepository.save(entidad);
    }

    public void eliminar(Integer id) {
        Cancion cancion = cancionRepository.findById(id)
                .orElseThrow(() -> new CancionNoEncontradaException("No existe canción con id: " + id));
        cancion.setActivo(false);
        cancionRepository.save(cancion);
    }

//    @Transactional(readOnly = true)
//    public Page<CancionResponseDto> buscarPorTituloPaginado(String titulo, Pageable pageable) {
//        Page<Cancion> page = (titulo == null || titulo.isEmpty())
//                ? cancionRepository.findByActivoTrue(pageable)
//                : cancionRepository.buscarPorTituloActivo(titulo, pageable);
//        return page.map(cancionMapper::toResponseDto);
//    }

    @Override
    @Transactional(readOnly = true)
    public Page<CancionResponseDto> buscarPorTituloActivoPaginado(String titulo, Pageable pageable) {
        Page<Cancion> page = cancionRepository.buscarActivasConArtistaActivo(titulo, pageable);
        return page.map(cancionMapper::toResponseDto);
    }



    @Override
    @Transactional(readOnly = true)
    public List<CancionResponseDto> listarInactivas() {
        return cancionRepository.findByActivoFalse()
                .stream()
                .map(cancionMapper::toResponseDto)
                .toList();
    }

    @Override
    public void reactivarCancion(Integer id) {
        Cancion cancion = cancionRepository.findById(id)
                .orElseThrow(() -> new CancionNoEncontradaException("No existe canción con id: " + id));
        cancion.setActivo(true);
        cancionRepository.save(cancion);
    }

}
