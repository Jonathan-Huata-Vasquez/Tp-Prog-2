package biblioteca.biblioteca.infrastructure.persistence.jpa.spring;


import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.AutorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutorSpringDataRepository extends JpaRepository<AutorEntity, Integer> {
}
