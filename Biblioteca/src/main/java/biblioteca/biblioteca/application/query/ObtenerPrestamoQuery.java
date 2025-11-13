package biblioteca.biblioteca.application.query;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ObtenerPrestamoQuery {
    @NotNull Integer idPrestamo;
}
