package biblioteca.biblioteca.infrastructure.persistence.jpa.spring;

import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RolSpringDataRepository extends JpaRepository<RolEntity, Integer> {
    Optional<RolEntity> findByNombre(String nombre);
    List<RolEntity> findByNombreIn(Collection<String> nombres);
}

