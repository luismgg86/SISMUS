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

/**
 * Mapper para conversion entre Usuario y sus DTOs
 * Incluye codificación de contraseñas y mapeo de roles
 */
@Component
public class UsuarioMapper {

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioMapper(ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponseDto toResponseDto(Usuario entity) {
        UsuarioResponseDto dto = new UsuarioResponseDto();
        dto.setId(entity.getId());
        dto.setNombreCompleto(entity.getNombre() + " " + entity.getApPaterno());
        dto.setCorreo(entity.getCorreo());
        dto.setNickname(entity.getNickname());
        dto.setActivo(entity.isActivo());

        Set<String> roles = entity.getRoles().stream()
                .map(Rol::getNombre)
                .collect(Collectors.toSet());
        dto.setRoles(roles);

        return dto;
    }

    public Usuario toEntity(UsuarioRegistroDto dto, Rol rolPorDefecto) {
        Usuario entity = modelMapper.map(dto, Usuario.class);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.setRoles(Set.of(rolPorDefecto));
        return entity;
    }
}
