package biblioteca.biblioteca.infrastructure.persistence.jpa.adapter;

import biblioteca.biblioteca.domain.exception.ReglaDeNegocioException;
import biblioteca.biblioteca.domain.model.Rol;
import biblioteca.biblioteca.domain.model.Usuario;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.RolEntity;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.UsuarioEntity;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    public Usuario toDomain(@NonNull UsuarioEntity e) {
        Set<Rol> roles = e.getRoles().stream()
                .map(r -> Rol.valueOf(r.getNombre()))
                .collect(Collectors.toSet());
        if (roles.isEmpty()) throw new ReglaDeNegocioException("El usuario debe tener al menos un rol");
        return Usuario.rehidratar(
                e.getId(),
                e.getNombreCompleto(),
                e.getDni(),
                e.getEmail(),
                e.getPasswordHash(),
                roles,
                e.getLectorId()
        );
    }

    /** email se normaliza a minúsculas en dominio; preservamos ese valor aquí */
    public UsuarioEntity toEntity(@NonNull Usuario u, Set<RolEntity> rolEntities) {
        UsuarioEntity e = new UsuarioEntity();
        e.setId(u.getId());
        e.setNombreCompleto(u.getNombreCompleto());
        e.setDni(u.getDni());
        e.setEmail(u.getEmail());
        e.setPasswordHash(u.getPasswordHash());
        e.setLectorId(u.getLectorId());
        e.setRoles(rolEntities);
        return e;
    }
}
