package biblioteca.biblioteca.web.mapper;

import biblioteca.biblioteca.domain.model.Prestamo;
import biblioteca.biblioteca.web.dto.PrestamoDto;
import org.springframework.stereotype.Component;

@Component
public class PrestamoDtoMapper {
    public PrestamoDto toDto(Prestamo p) {
        if (p == null) return null;
        return PrestamoDto.builder()
                .id(p.getIdPrestamo())
                .idLector(p.getIdLector())
                .idCopia(p.getIdCopia())
                .fechaInicio(p.getFechaInicio())
                .fechaVencimiento(p.getFechaVencimiento())
                .fechaDevolucion(p.getFechaDevolucion())
                .build();
    }
}
