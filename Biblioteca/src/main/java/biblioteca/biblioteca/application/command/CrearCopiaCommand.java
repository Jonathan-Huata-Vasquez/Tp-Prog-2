package biblioteca.biblioteca.application.command;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CrearCopiaCommand {
    @NotNull Integer idLibro;
}
