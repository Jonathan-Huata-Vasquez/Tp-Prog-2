package biblioteca.biblioteca.web.dto;

import biblioteca.biblioteca.domain.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class UsuarioFormDto {
    Integer id; // null en creación
    @NotBlank String nombreCompleto;
    @NotBlank String dni; // solo creación, en edición se puede ignorar (mostrar read-only)
    @Email @NotBlank String email;
    @NotEmpty Set<Rol> roles;
    Integer lectorId;
    // password opcional: requerido en creación, vacío en edición => validación manual en controlador
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    String password;

    public static UsuarioFormDto fromDto(UsuarioDto dto) {
        if (dto == null) return null;
        return UsuarioFormDto.builder()
                .id(dto.getId())
                .nombreCompleto(dto.getNombreCompleto())
                .dni(dto.getDni())
                .email(dto.getEmail())
                .roles(dto.getRoles())
                .lectorId(dto.getLectorId())
                .build();
    }
}
