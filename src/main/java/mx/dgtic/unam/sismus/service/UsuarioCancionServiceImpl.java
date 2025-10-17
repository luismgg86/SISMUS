package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.model.Cancion;
import mx.dgtic.unam.sismus.model.Usuario;
import mx.dgtic.unam.sismus.model.UsuarioCancion;
import mx.dgtic.unam.sismus.repository.CancionRepository;
import mx.dgtic.unam.sismus.repository.UsuarioCancionRepository;
import mx.dgtic.unam.sismus.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class UsuarioCancionServiceImpl implements UsuarioCancionService {

    private UsuarioCancionRepository usuarioCancionRepository;
    private UsuarioRepository usuarioRepository;
    private CancionRepository cancionRepository;

    public UsuarioCancionServiceImpl(UsuarioCancionRepository usuarioCancionRepository, UsuarioRepository usuarioRepository, CancionRepository cancionRepository) {
        this.usuarioCancionRepository = usuarioCancionRepository;
        this.usuarioRepository = usuarioRepository;
        this.cancionRepository = cancionRepository;
    }

    @Override
    @Transactional
    public UsuarioCancion registrarDescarga(UsuarioCancion usuarioCancion) {
        return usuarioCancionRepository.save(usuarioCancion);
    }

    @Override
    @Transactional
    public UsuarioCancion registrarDescargaPorIds(Integer usuarioId, Integer cancionId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId));

        Cancion cancion = cancionRepository.findById(cancionId)
                .orElseThrow(() -> new IllegalArgumentException("Canción no encontrada con ID: " + cancionId));

        UsuarioCancion registro = new UsuarioCancion();
        registro.setUsuario(usuario);
        registro.setCancion(cancion);
        registro.setFechaDescarga(LocalDate.now());

        return usuarioCancionRepository.save(registro);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioCancion> obtenerDescargasPorUsuario(Integer usuarioId) {
        return usuarioCancionRepository.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioCancion> obtenerDescargasPorCancion(Integer cancionId) {
        return usuarioCancionRepository.findByCancionId(cancionId);
    }

    @Override
    public void eliminarDescarga(Integer id) {
        if (!usuarioCancionRepository.existsById(id)) {
            throw new IllegalArgumentException("No se encontró la descarga con ID: " + id);
        }
        usuarioCancionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeDescarga(Integer usuarioId, Integer cancionId) {
        return usuarioCancionRepository.existsByUsuarioIdAndCancionId(usuarioId, cancionId);
    }
}
