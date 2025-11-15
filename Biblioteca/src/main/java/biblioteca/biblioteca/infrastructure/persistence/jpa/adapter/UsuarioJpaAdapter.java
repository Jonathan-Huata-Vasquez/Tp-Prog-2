package biblioteca.biblioteca.infrastructure.persistence.jpa.adapter;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.domain.model.Rol;
import biblioteca.biblioteca.domain.model.Usuario;
import biblioteca.biblioteca.domain.port.IUsuarioRepository;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.RolEntity;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.UsuarioEntity;
import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.RolSpringDataRepository;
import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.UsuarioSpringDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UsuarioJpaAdapter implements IUsuarioRepository {

    private final UsuarioSpringDataRepository repo;
    private final RolSpringDataRepository rolRepo;
    private final UsuarioMapper mapper;

    @Override
    @Transactional
    public Usuario guardar(Usuario usuario) {
        //Tomamos los nombres (enum.name()) de los roles del dominio
        Set<String> nombres = usuario.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        List<RolEntity> encontrados = rolRepo.findByNombreIn(nombres);
        Map<String, RolEntity> porNombre = encontrados.stream()
                .collect(Collectors.toMap(RolEntity::getNombre, r -> r));


        List<String> faltantes = nombres.stream()
                .filter(n -> !porNombre.containsKey(n))
                .sorted()
                .toList();

        if (!faltantes.isEmpty()) {
            // Podés usar una excepción de configuración más específica si querés
            throw new EntidadNoEncontradaException(
                    "Roles no configurados en BD: " + String.join(", ", faltantes)
            );
        }

        Set<RolEntity> rolEntities = nombres.stream()
                .map(porNombre::get)
                .collect(Collectors.toSet());

        UsuarioEntity entity = mapper.toEntity(usuario, rolEntities);
        UsuarioEntity saved = repo.save(entity);

        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario porId(Integer id) {
        return repo.findById(id).map(mapper::toDomain).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario porEmail(String email) {
        return repo.findByEmail(email == null ? null : email.toLowerCase()).map(mapper::toDomain).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario porDni(String dni) {
        return repo.findByDni(dni).map(mapper::toDomain).orElse(null);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> todos() {
        return repo.findAll().stream().map(mapper::toDomain).toList();
    }
}
