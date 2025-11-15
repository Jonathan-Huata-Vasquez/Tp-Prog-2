package biblioteca.biblioteca.infrastructure.persistence.jpa.spring;

import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioSpringDataRepository extends JpaRepository<UsuarioEntity, Integer> {
    Optional<UsuarioEntity> findByEmail(String email);
    Optional<UsuarioEntity> findByDni(String dni);
}
