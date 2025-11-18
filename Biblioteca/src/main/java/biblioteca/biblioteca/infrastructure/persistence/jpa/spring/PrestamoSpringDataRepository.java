package biblioteca.biblioteca.infrastructure.persistence.jpa.spring;

import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.PrestamoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PrestamoSpringDataRepository extends JpaRepository<PrestamoEntity, Integer> {

    List<PrestamoEntity> findByLectorIdAndFechaDevolucionIsNull(Integer lectorId);

    Optional<PrestamoEntity> findByLectorIdAndCopiaIdAndFechaDevolucionIsNull(Integer lectorId, Integer copiaId);
    
    List<PrestamoEntity> findByLectorId(Integer lectorId);
    
    // Query optimizada: ordena por activos primero (nulls first), luego por fecha vencimiento
    @Query("SELECT p FROM PrestamoEntity p WHERE p.lectorId = :lectorId ORDER BY p.fechaDevolucion NULLS FIRST, p.fechaVencimiento ASC")
    List<PrestamoEntity> findByLectorIdOrderByFechaDevolucionNullsFirstFechaVencimientoAsc(@Param("lectorId") Integer lectorId);
    
    // Contadores optimizados
    int countByLectorIdAndFechaDevolucionIsNull(Integer lectorId);
    int countByLectorIdAndFechaDevolucionIsNullAndFechaVencimientoBefore(Integer lectorId, LocalDate fecha);
    int countByLectorIdAndFechaDevolucionIsNotNull(Integer lectorId);

    int countByCopiaId(Integer copiaId);
}
