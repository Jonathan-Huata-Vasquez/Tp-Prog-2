package biblioteca.biblioteca.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ActualizarPasswordCommand {
    @NotNull Integer idUsuario;
    @NotBlank String passwordHash;
}
