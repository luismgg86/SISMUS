package mx.dgtic.unam.sismus.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lista_cancion", uniqueConstraints = @UniqueConstraint(columnNames = {"lista_id", "cancion_id"}))
public class ListaCancion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lista_cancion_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lista_id", nullable = false)
    private Lista lista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancion_id", nullable = false)
    private Cancion cancion;

    @Column(name = "fecha_agregada")
    private LocalDate fechaAgregada;
}
