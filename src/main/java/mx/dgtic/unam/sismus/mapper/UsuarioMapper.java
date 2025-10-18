package mx.dgtic.unam.sismus.mapper;

import mx.dgtic.unam.sismus.dto.UsuarioRegistroDto;
import mx.dgtic.unam.sismus.dto.UsuarioResponseDto;
import mx.dgtic.unam.sismus.model.Usuario;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UsuarioMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public UsuarioResponseDto toResponseDto(Usuario usuario) {
        UsuarioResponseDto dto = modelMapper.map(usuario, UsuarioResponseDto.class);
        dto.setNombreCompleto(usuario.getNombre() + " " + usuario.getApPaterno());
        return dto;
    }

    public Usuario toEntity(UsuarioRegistroDto dto) {
        Usuario usuario = modelMapper.map(dto, Usuario.class);
        usuario.setContrasena(encoder.encode(dto.getContrasena()));
        usuario.setRol((short) 1); // Usuario normal
        return usuario;
    }
}
