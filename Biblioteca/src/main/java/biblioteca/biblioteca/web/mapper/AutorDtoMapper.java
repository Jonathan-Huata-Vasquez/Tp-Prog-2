package biblioteca.biblioteca.web.mapper;

import biblioteca.biblioteca.domain.model.Autor;
import biblioteca.biblioteca.web.dto.AutorDto;
import org.springframework.stereotype.Component;

@Component
public class AutorDtoMapper {
    public AutorDto toDto(Autor a) {
        if (a == null) return null;
        return AutorDto.builder()
                .id(a.getIdAutor())
                .nombre(a.getNombre())
                .fechaNacimiento(a.getFechaNacimiento())
                .nacionalidad(a.getNacionalidad())
                .build();
    }
}
