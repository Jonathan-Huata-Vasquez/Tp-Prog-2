package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CopiaDetalleDto {
    Integer id;
    String estado; // Estado legible: "Disponible", "Prestado", "En reparación", "Con retraso"
}
