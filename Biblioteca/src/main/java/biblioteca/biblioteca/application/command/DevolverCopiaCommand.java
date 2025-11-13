package biblioteca.biblioteca.application.command;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DevolverCopiaCommand {
    Integer idUsuario;
    @NotNull Integer idLector;
    @NotNull Integer idCopia;
    @NotNull Boolean enviarAReparacion;
}
