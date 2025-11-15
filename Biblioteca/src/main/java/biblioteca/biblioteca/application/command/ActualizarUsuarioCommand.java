package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.domain.model.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class ActualizarUsuarioCommand {
    @NotNull Integer idUsuario;
    @NotBlank String nombreCompleto;
    @NotBlank String email;
    @NotEmpty Set<Rol> roles;
    Integer lectorId; // opcional
}
