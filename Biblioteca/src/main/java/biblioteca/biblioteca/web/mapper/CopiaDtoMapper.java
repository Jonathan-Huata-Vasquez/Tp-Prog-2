package biblioteca.biblioteca.web.mapper;

import biblioteca.biblioteca.domain.model.Copia;
import biblioteca.biblioteca.web.dto.CopiaDto;
import org.springframework.stereotype.Component;

@Component
public class CopiaDtoMapper {
    public CopiaDto toDto(Copia c) {
        if (c == null) return null;
        return CopiaDto.builder()
                .id(c.getIdCopia())
                .idLibro(c.getIdLibro())
                .estado(c.getEstado())
                .build();
    }
}
