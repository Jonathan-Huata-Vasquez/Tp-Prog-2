package biblioteca.biblioteca.infrastructure.persistence.jpa.adapter;

import biblioteca.biblioteca.domain.model.Editorial;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.EditorialEntity;
import org.springframework.stereotype.Component;

@Component
public class EditorialMapper {

    public Editorial toDomain(EditorialEntity e) {
        if (e == null) return null;
        return Editorial.rehidratar(e.getId(), e.getNombre());
    }

    public EditorialEntity toEntity(Editorial d) {
        if (d == null) return null;
        return new EditorialEntity(d.getIdEditorial(), d.getNombre());
    }
}
