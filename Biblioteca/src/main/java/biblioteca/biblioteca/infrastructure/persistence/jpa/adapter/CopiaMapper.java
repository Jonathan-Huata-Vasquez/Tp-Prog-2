package biblioteca.biblioteca.infrastructure.persistence.jpa.adapter;

import biblioteca.biblioteca.domain.model.Copia;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.CopiaEntity;
import org.springframework.stereotype.Component;

@Component
public class CopiaMapper {

    public Copia toDomain(CopiaEntity e) {
        if (e == null) return null;
        return Copia.rehidratar(e.getId(), e.getLibroId(), e.getEstado());
    }

    public CopiaEntity toEntity(Copia d) {
        if (d == null) return null;
        return new CopiaEntity(d.getIdCopia(), d.getIdLibro(), d.getEstado());
    }
}
