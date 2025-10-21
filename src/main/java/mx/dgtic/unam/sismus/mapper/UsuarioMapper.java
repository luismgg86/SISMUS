package mx.dgtic.unam.sismus.mapper;

import mx.dgtic.unam.sismus.dto.UsuarioRegistroDto;
import mx.dgtic.unam.sismus.dto.UsuarioResponseDto;
import mx.dgtic.unam.sismus.model.Usuario;
import mx.dgtic.unam.sismus.model.Rol;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioMapper(ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponseDto toResponseDto(Usuario usuario) {
        UsuarioResponseDto dto = new UsuarioResponseDto();
        dto.setId(usuario.getId());
        dto.setNombreCompleto(usuario.getNombre() + " " + usuario.getApPaterno());
        dto.setCorreo(usuario.getCorreo());
        dto.setNickname(usuario.getNickname());
        dto.setActivo(usuario.getActivo());

        // Convertimos Set<Rol> → Set<String>
        Set<String> nombresRoles = usuario.getRoles()
                .stream()
                .map(Rol::getNombre) // "USER", "ADMIN"
                .collect(Collectors.toSet());
        dto.setRoles(nombresRoles);

        return dto;
    }

    public Usuario toEntity(UsuarioRegistroDto dto, Rol rolPorDefecto) {
        Usuario usuario = modelMapper.map(dto, Usuario.class);

        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));

        usuario.setRoles(Set.of(rolPorDefecto));

        return usuario;
    }
}
