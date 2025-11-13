package biblioteca.biblioteca.web.mapper;

import biblioteca.biblioteca.domain.model.Editorial;
import biblioteca.biblioteca.web.dto.EditorialDto;
import org.springframework.stereotype.Component;

@Component
public class EditorialDtoMapper {
    public EditorialDto toDto(Editorial e) {
        if (e == null) return null;
        return EditorialDto.builder()
                .id(e.getIdEditorial())
                .nombre(e.getNombre())
                .build();
    }
}
