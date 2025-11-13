package biblioteca.biblioteca.infrastructure.persistence.jpa.spring;


import biblioteca.biblioteca.domain.model.EstadoCopia;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.CopiaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CopiaSpringDataRepository extends JpaRepository<CopiaEntity, Integer> {
    List<CopiaEntity> findByLibroIdAndEstado(Integer libroId, EstadoCopia estado);
    List<CopiaEntity> findByLibroId(Integer libroId);
}
