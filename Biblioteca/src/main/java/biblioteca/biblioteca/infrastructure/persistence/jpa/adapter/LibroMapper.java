package biblioteca.biblioteca.infrastructure.persistence.jpa.adapter;

import biblioteca.biblioteca.domain.model.Libro;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.LibroEntity;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class LibroMapper {

    public Libro toDomain(@NonNull LibroEntity e) {
        return Libro.rehidratar(
                e.getId(),
                e.getTitulo(),
                e.getAnioPublicacion(),
                e.getAutorId(),
                e.getEditorialId(),
                e.getCategoria(),
                e.getDescripcion() == null ? "" : e.getDescripcion()
        );
    }

    public LibroEntity toEntity(@NonNull Libro d) {
        return new LibroEntity(
                d.getIdLibro(),
                d.getTitulo(),
                d.getAnioPublicacion(),
                d.getIdAutor(),
                d.getIdEditorial(),
                d.getCategoria(),
                d.getDescripcion() == null ? "" : d.getDescripcion()
        );
    }
}
