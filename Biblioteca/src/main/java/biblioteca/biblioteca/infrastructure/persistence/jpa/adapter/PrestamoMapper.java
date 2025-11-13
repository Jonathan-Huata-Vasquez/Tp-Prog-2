package biblioteca.biblioteca.infrastructure.persistence.jpa.adapter;



import biblioteca.biblioteca.domain.model.Prestamo;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.PrestamoEntity;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class PrestamoMapper {

    /** Entity → Dominio */
    public Prestamo toDomain(@NonNull PrestamoEntity e) {
        return Prestamo.rehidratar(
                e.getId(),
                e.getLectorId(),
                e.getCopiaId(),
                e.getFechaInicio(),
                e.getFechaVencimiento(),
                e.getFechaDevolucion()
        );
    }

    /** Dominio → Entity */
    public PrestamoEntity toEntity(@NonNull Prestamo d) {
        return new PrestamoEntity(
                d.getIdPrestamo(),
                d.getIdLector(),
                d.getIdCopia(),
                d.getFechaInicio(),
                d.getFechaVencimiento(),
                d.getFechaDevolucion()
        );
    }
}