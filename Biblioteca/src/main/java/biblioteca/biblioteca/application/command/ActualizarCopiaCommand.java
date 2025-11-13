package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.domain.model.EstadoCopia;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ActualizarCopiaCommand {
    @NotNull Integer idCopia;
    @NotNull EstadoCopia nuevoEstado;
}
