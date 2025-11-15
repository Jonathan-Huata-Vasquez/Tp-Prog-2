package biblioteca.biblioteca.web.mapper;

import biblioteca.biblioteca.web.dto.UsuarioDto;
import biblioteca.biblioteca.domain.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioDtoMapper {
    public UsuarioDto toDto(Usuario u) {
        if (u == null) return null;
        return UsuarioDto.builder()
                .id(u.getId())
                .nombreCompleto(u.getNombreCompleto())
                .dni(u.getDni())
                .email(u.getEmail())
                .roles(u.getRoles())
                .lectorId(u.getLectorId())
                .build();
    }
}

