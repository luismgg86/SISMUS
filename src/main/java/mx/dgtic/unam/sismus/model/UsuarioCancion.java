    package mx.dgtic.unam.sismus.model;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.time.LocalDate;

    @Data @NoArgsConstructor @AllArgsConstructor
    @Entity
    @Table(name = "usuario_cancion")
    public class UsuarioCancion {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "usuario_cancion_id")
        private Integer id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "usuario_id", nullable = false)
        private Usuario usuario;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "cancion_id", nullable = false)
        private Cancion cancion;

        @Column(name = "fecha_descarga")
        private LocalDate fechaDescarga;
    }
