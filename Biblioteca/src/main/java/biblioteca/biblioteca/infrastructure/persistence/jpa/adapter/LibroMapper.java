package biblioteca.biblioteca.infrastructure.persistence.jpa.adapter;

import biblioteca.biblioteca.domain.model.Libro;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.LibroEntity;
import org.springframework.stereotype.Component;

@Component
public class LibroMapper {

    public Libro toDomain(LibroEntity e) {
        if (e == null) return null;
        return Libro.rehidratar(
                e.getId(),
                e.getTitulo(),
                e.getAnioPublicacion(),
                e.getAutorId(),
                e.getEditorialId(),
                e.getCategoria()
        );
    }

    public LibroEntity toEntity(Libro d) {
        if (d == null) return null;
        return new LibroEntity(
                d.getIdLibro(),
                d.getTitulo(),
                d.getAnioPublicacion(),
                d.getIdAutor(),
                d.getIdEditorial(),
                d.getCategoria()
        );
    }
}
