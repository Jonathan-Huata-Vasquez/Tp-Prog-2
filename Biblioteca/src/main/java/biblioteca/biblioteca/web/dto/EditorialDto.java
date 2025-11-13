package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EditorialDto {
    Integer id;
    String nombre;
}
