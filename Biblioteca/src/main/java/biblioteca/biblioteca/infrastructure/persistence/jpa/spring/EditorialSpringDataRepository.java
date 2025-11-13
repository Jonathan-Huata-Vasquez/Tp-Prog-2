package biblioteca.biblioteca.infrastructure.persistence.jpa.spring;

import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.EditorialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EditorialSpringDataRepository extends JpaRepository<EditorialEntity, Integer> {
}
