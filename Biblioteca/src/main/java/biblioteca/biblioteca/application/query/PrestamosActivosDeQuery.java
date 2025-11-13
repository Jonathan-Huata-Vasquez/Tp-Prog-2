package biblioteca.biblioteca.application.query;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PrestamosActivosDeQuery {
    @NotNull Integer idLector;
}
