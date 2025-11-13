package biblioteca.biblioteca.infrastructure.persistence.jpa.spring;

import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.LibroEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibroSpringDataRepository extends JpaRepository<LibroEntity, Integer> {
    List<LibroEntity> findByAutorId(Integer autorId);
}
