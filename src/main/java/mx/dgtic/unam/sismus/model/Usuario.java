package mx.dgtic.unam.sismus.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Integer id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(name = "ap_paterno", length = 50)
    private String apPaterno;

    @Column(name = "ap_materno", length = 50)
    private String apMaterno;

    @Column(unique = true, length = 100)
    private String correo;

    @Column(unique = true, length = 30)
    private String nickname;

    @Column(length = 60)
    private String contrasena;

    private Short rol;

    @ToString.Exclude @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsuarioCancion> cancionesDescargadas;

    @ToString.Exclude @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lista> listas;

}
