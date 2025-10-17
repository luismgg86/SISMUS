package mx.dgtic.unam.sismus.config;

import jakarta.annotation.PostConstruct;
import mx.dgtic.unam.sismus.dto.*;
import mx.dgtic.unam.sismus.model.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomMapperConfig {

    private final ModelMapper mapper;

    public CustomMapperConfig(ModelMapper mapper) {
        this.mapper = mapper;
    }

    @PostConstruct
    public void configure() {

        /* ==========================================================
         *  CANCION → CANCIONDTO
         * ========================================================== */
        mapper.addMappings(new PropertyMap<Cancion, CancionDTO>() {
            @Override
            protected void configure() {
                // Artista.nombre → CancionDTO.artista
                map().setArtista(source.getArtista().getNombre());
                // Genero.nombre → CancionDTO.genero
                map().setGenero(source.getGenero().getNombre());
            }
        });

        /* ==========================================================
         *  LISTA → LISTADTO
         * ========================================================== */
        mapper.addMappings(new PropertyMap<Lista, ListaDTO>() {
            @Override
            protected void configure() {
                // Usuario.nickname → ListaDTO.usuarioNickname
                map().setUsuarioNickname(source.getUsuario().getNickname());
            }
        });

        /* ==========================================================
         *  LISTACANCION → LISTACANCIONDTO
         * ========================================================== */
        mapper.addMappings(new PropertyMap<ListaCancion, ListaCancionDTO>() {
            @Override
            protected void configure() {
                // Mapear Cancion completa como subobjeto DTO
                using(ctx -> mapper.map(((ListaCancion) ctx.getSource()).getCancion(), CancionDTO.class))
                        .map(source, destination.getCancion());
            }
        });

        /* ==========================================================
         *  USUARIO → USUARIODTO
         * ========================================================== */
        mapper.addMappings(new PropertyMap<Usuario, UsuarioDTO>() {
            @Override
            protected void configure() {
                // Mapea las listas del usuario a DTOs
                using(ctx -> ((Usuario) ctx.getSource()).getListas()
                        .stream()
                        .map(lista -> mapper.map(lista, ListaDTO.class))
                        .toList())
                        .map(source, destination.getListas());
            }
        });

        /* ==========================================================
         *  USUARIOCANCION → USUARIOCANCIONDTO
         * ========================================================== */
        mapper.addMappings(new PropertyMap<UsuarioCancion, UsuarioCancionDTO>() {
            @Override
            protected void configure() {
                // Cancion.id → UsuarioCancionDTO.cancionId
                map().setCancionId(source.getCancion().getId());
                // Cancion.titulo → UsuarioCancionDTO.tituloCancion
                map().setTituloCancion(source.getCancion().getTitulo());
                // Cancion.artista.nombre → UsuarioCancionDTO.artistaNombre
                map().setArtistaNombre(source.getCancion().getArtista().getNombre());
            }
        });
    }
}
