package biblioteca.biblioteca.application.query;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class ObtenerCopiaQuery {
    @NotNull Integer idCopia;
}
