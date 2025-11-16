package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class CatalogoResultDto {
    String criterio;            // q
    Integer totalLibros;        // conteo (post filtro)
    List<LibroCatalogoItemDto> items;
}
