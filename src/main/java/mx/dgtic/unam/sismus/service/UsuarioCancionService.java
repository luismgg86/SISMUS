package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.dto.CancionResponseDto;
import mx.dgtic.unam.sismus.dto.UsuarioCancionDto;

import java.util.List;

public interface UsuarioCancionService {

    UsuarioCancionDto registrarDescarga(Integer usuarioId, Integer cancionId);
    void registrarDescargas(Integer usuarioId, List<CancionResponseDto> canciones);
    List<UsuarioCancionDto> obtenerDescargasPorUsuario(Integer usuarioId);
    List<UsuarioCancionDto> obtenerDescargasPorCancion(Integer cancionId);
    void eliminarDescarga(Integer id);
    boolean existeDescarga(Integer usuarioId, Integer cancionId);
}
