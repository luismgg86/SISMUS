package mx.dgtic.unam.sismus.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "artista")
public class Artista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artista_id")
    private Integer artistaId;

    @Column(length = 5, nullable = false, unique = true)
    private String clave;

    @Column(length = 100, nullable = false)
    private String nombre;

    @ToString.Exclude @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "artista", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cancion> canciones;

    @Column(nullable = false)
    private boolean activo = true;

}
