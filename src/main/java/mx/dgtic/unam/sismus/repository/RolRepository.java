package mx.dgtic.unam.sismus.repository;

import mx.dgtic.unam.sismus.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface RolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByNombre(String nombre);
    Set<Rol> findByNombreIn(Set<String> roles);
}
