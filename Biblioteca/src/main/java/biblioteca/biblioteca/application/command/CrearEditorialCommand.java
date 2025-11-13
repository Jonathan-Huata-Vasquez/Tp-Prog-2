package biblioteca.biblioteca.application.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CrearEditorialCommand {
    @NotBlank String nombre;
}