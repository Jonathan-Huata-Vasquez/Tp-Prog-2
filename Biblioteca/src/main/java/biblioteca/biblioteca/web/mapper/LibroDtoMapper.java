package biblioteca.biblioteca.web.mapper;

import biblioteca.biblioteca.domain.model.Libro;
import biblioteca.biblioteca.web.dto.LibroDto;
import org.springframework.stereotype.Component;

@Component
public class LibroDtoMapper {
    public LibroDto toDto(Libro l) {
        if (l == null) return null;
        return LibroDto.builder()
                .id(l.getIdLibro())
                .titulo(l.getTitulo())
                .anioPublicacion(l.getAnioPublicacion())
                .idAutor(l.getIdAutor())
                .idEditorial(l.getIdEditorial())
                .categoria(l.getCategoria())
                .build();
    }
}
