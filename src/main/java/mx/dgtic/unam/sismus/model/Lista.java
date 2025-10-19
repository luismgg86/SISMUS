package mx.dgtic.unam.sismus.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Lista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lista_id")
    private Integer id;

    @Column(length = 50)
    private String nombre;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "lista", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ListaCancion> canciones = new ArrayList<>();

}
