package biblioteca.biblioteca.application.command;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EliminarUsuarioCommand {
    @NotNull Integer idUsuario;
}
