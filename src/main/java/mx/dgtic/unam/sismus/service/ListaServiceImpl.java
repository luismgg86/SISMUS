package mx.dgtic.unam.sismus.service;

import jakarta.transaction.Transactional;
import mx.dgtic.unam.sismus.model.Cancion;
import mx.dgtic.unam.sismus.model.Lista;
import mx.dgtic.unam.sismus.repository.CancionRepository;
import mx.dgtic.unam.sismus.repository.ListaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ListaServiceImpl implements ListaService {

    private final ListaRepository listaRepository;
    private final CancionRepository cancionRepository;

    public ListaServiceImpl(ListaRepository listaRepository, CancionRepository cancionRepository) {
        this.listaRepository = listaRepository;
        this.cancionRepository = cancionRepository;
    }

    @Override
    public List<Lista> listarTodas() {
        return listaRepository.findAllConRelaciones();
    }

    @Override
    public Optional<Lista> buscarPorId(Integer id) {
        return listaRepository.findById(id);
    }

    @Override
    public Lista guardar(Lista lista) {
        return listaRepository.save(lista);
    }

    @Override
    public void eliminar(Integer id) {
        listaRepository.deleteById(id);
    }

    /* ===== Funcionalidad de negocio ===== */

    @Override
    public Lista obtenerConRelaciones(Integer id) {
        return listaRepository.findByIdConRelaciones(id);
    }

    @Override
    public void agregarCancionALista(Integer listaId, Integer cancionId) {
        Lista lista = listaRepository.findByIdConRelaciones(listaId);
        if (lista == null) {
            throw new IllegalArgumentException("Lista no encontrada con ID: " + listaId);
        }

        Cancion cancion = cancionRepository.findById(cancionId)
                .orElseThrow(() -> new IllegalArgumentException("Canción no encontrada con ID: " + cancionId));

        if (lista.getCanciones().contains(cancion)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La canción ya está en la playlist");
        }

        lista.getCanciones().add(cancion);
        listaRepository.save(lista);
    }

    @Override
    public void eliminarCancionDeLista(Integer listaId, Integer cancionId) {
        Lista lista = listaRepository.findByIdConRelaciones(listaId);
        if (lista == null) {
            throw new IllegalArgumentException("Lista no encontrada con ID: " + listaId);
        }

        lista.getCanciones().removeIf(c -> c.getId().equals(cancionId));
        listaRepository.save(lista);
    }

    @Override
    public List<Lista> obtenerListasPorUsuario(String nickname) {
        return listaRepository.findByUsuario_Nickname(nickname);
    }

    @Override
    public Lista crearPlaylist(String nombre) {
        Lista nuevaLista = new Lista();
        nuevaLista.setNombre(nombre);
        nuevaLista.setFechaCreacion(LocalDate.now());
        // Esto es temporal ya que no hemos visto sesiones
        // Aquí asignar el usuario actual
        // nuevaLista.setUsuario(usuarioActual);
        return listaRepository.save(nuevaLista);
    }


}
