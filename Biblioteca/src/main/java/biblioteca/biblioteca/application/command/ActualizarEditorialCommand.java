package biblioteca.biblioteca.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ActualizarEditorialCommand {
    @NotNull Integer idEditorial;
    @NotBlank String nombre;
}
