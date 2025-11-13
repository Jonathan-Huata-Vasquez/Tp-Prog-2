package biblioteca.biblioteca.web.mapper;

import biblioteca.biblioteca.domain.model.Lector;
import biblioteca.biblioteca.web.dto.LectorDto;
import org.springframework.stereotype.Component;

@Component
public class LectorDtoMapper {

    public LectorDto toDto(Lector l) {
        if (l == null) return null;
        return LectorDto.builder()
                .id(l.getIdLector())
                .nombre(l.getNombre())
                .bloqueadoHasta(l.getBloqueadoHasta())
                .prestamosActivos(l.prestamosVigentes().size())
                .build();
    }
}
