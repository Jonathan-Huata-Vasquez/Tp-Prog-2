package biblioteca.biblioteca.web.dto;

import biblioteca.biblioteca.domain.model.Rol;
import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class UsuarioDto {
    Integer id;
    String nombreCompleto;
    String dni;
    String email;
    Set<Rol> roles;
    Integer lectorId; // nullable
}

