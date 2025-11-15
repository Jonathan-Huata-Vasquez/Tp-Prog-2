package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.domain.model.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class CrearUsuarioCommand {
    @NotBlank String nombreCompleto;
    @NotBlank String dni;
    @NotBlank String email;
    @NotBlank String passwordHash;
    @NotEmpty Set<Rol> roles;
    Integer lectorId; // opcional
}
