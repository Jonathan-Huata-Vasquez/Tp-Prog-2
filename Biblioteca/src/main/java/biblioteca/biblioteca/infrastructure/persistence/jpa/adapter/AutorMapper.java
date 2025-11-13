package biblioteca.biblioteca.infrastructure.persistence.jpa.adapter;

import biblioteca.biblioteca.domain.model.Autor;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.AutorEntity;
import org.springframework.stereotype.Component;

@Component
public class AutorMapper {

    public Autor toDomain(AutorEntity e) {
        if (e == null) return null;
        return Autor.rehidratar(e.getId(), e.getNombre(), e.getFechaNacimiento(), e.getNacionalidad());
    }

    public AutorEntity toEntity(Autor d) {
        if (d == null) return null;
        return new AutorEntity(d.getIdAutor(), d.getNombre(), d.getFechaNacimiento(), d.getNacionalidad());
    }
}
