package mx.dgtic.unam.sismus.security.model;

import mx.dgtic.unam.sismus.model.Usuario;
import mx.dgtic.unam.sismus.model.Rol;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Implementación personalizada de UserDetails
 * que adapta la entidad Usuario a los requerimientos de Spring Security.
 */
public class UserDetailsImpl implements UserDetails {

    private final Usuario usuario;

    public UserDetailsImpl(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return usuario.getRoles().stream()
                .map(Rol::getNombre)
                .map(nombre -> new SimpleGrantedAuthority("ROLE_" + nombre))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    @Override
    public String getUsername() {
        return usuario.getNickname();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // No se maneja expiracion de cueta
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // No se maneja bloqueo de cuenta
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // No se maneja expiracion de credenciales
    }

    @Override
    public boolean isEnabled() {
        return usuario.isActivo();
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
