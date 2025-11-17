package biblioteca.biblioteca.infrastructure.persistence.jpa.spring;

import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.LectorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface LectorSpringDataRepository extends JpaRepository<LectorEntity, Integer> {

    // Lectores que siguen bloqueados en 'hastaFecha' (hastaFecha <= bloqueado_hasta)
    @Query("SELECT l FROM LectorEntity l WHERE l.bloqueadoHasta IS NOT NULL AND l.bloqueadoHasta >= :hastaFecha")
    List<LectorEntity> bloqueadosDesde(LocalDate hastaFecha);

    // Consultas para filtrado con paginación
    @Query("SELECT l FROM LectorEntity l WHERE l.bloqueadoHasta IS NOT NULL AND :fecha <= l.bloqueadoHasta")
    Page<LectorEntity> findBloqueados(@Param("fecha") LocalDate fecha, Pageable pageable);

    @Query("SELECT l FROM LectorEntity l WHERE l.bloqueadoHasta IS NULL OR :fecha > l.bloqueadoHasta")
    Page<LectorEntity> findHabilitados(@Param("fecha") LocalDate fecha, Pageable pageable);

    // Contadores para resumen
    @Query("SELECT COUNT(l) FROM LectorEntity l WHERE l.bloqueadoHasta IS NOT NULL AND :fecha <= l.bloqueadoHasta")
    long countBloqueados(@Param("fecha") LocalDate fecha);
}