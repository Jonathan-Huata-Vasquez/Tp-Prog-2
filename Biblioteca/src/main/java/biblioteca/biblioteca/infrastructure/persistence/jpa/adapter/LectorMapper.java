package biblioteca.biblioteca.infrastructure.persistence.jpa.adapter;


import biblioteca.biblioteca.domain.model.Lector;
import biblioteca.biblioteca.domain.model.Prestamo;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.LectorEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LectorMapper {

    public Lector toDomain(LectorEntity e) {
        // Base sin préstamos (los activos se inyectan aparte)
        return Lector.rehidratar(e.getId(), e.getNombre(), e.getBloqueadoHasta(), List.of());
    }

    public Lector toDomainWithPrestamos(LectorEntity e, List<Prestamo> prestamosActivos) {
        return Lector.rehidratar(e.getId(), e.getNombre(), e.getBloqueadoHasta(), prestamosActivos);
    }

    public LectorEntity toEntity(Lector d) {
        return new LectorEntity(d.getIdLector(), d.getNombre(), d.getBloqueadoHasta());
    }
}
