package mx.dgtic.unam.sismus.security.model;

import mx.dgtic.unam.sismus.model.Usuario;
import mx.dgtic.unam.sismus.model.Rol;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {

    private final Usuario usuario;

    public UserDetailsImpl(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return usuario.getRoles().stream()
                .map(Rol::getNombre) // "USER", "ADMIN"
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return usuario.getPassword(); // ✅ mapea al campo password de tu entidad
    }

    @Override
    public String getUsername() {
        return usuario.getNickname(); // ✅ login con nickname
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
