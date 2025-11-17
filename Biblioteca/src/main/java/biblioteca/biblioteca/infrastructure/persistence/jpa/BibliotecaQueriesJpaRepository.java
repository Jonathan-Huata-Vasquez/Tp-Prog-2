package biblioteca.biblioteca.infrastructure.persistence.jpa;

import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.PrestamoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio JPA especializado para pseudo-queries
 */
@Repository
public interface BibliotecaQueriesJpaRepository extends JpaRepository<PrestamoEntity, Integer> {
    
    /**
     * Pseudo-query nativa para obtener todos los préstamos activos ordenados por fecha de vencimiento.
     */
    @Query(value = """
        SELECT p.* FROM prestamo p 
        WHERE p.fecha_devolucion IS NULL 
        ORDER BY p.fecha_vencimiento ASC 
        LIMIT :limite
        """, nativeQuery = true)
    List<PrestamoEntity> findPrestamosDestacadosOptimizado(@Param("limite") int limite);
    
    /**
     * Pseudo-query nativa para obtener estadísticas agregadas en una sola consulta.
     * Utiliza CASE WHEN para calcular múltiples contadores de forma optimizada.
     */
    @Query(value = """
        SELECT 
            COUNT(*) as total_activos,
            COUNT(CASE WHEN p.fecha_vencimiento < :fecha THEN 1 END) as total_vencidos,
            COUNT(CASE WHEN p.fecha_vencimiento = :fecha THEN 1 END) as vencen_hoy,
            COUNT(CASE WHEN p.fecha_vencimiento > :fecha AND p.fecha_vencimiento <= :fechaLimite THEN 1 END) as proximos_vencimientos
        FROM prestamo p 
        WHERE p.fecha_devolucion IS NULL
        """, nativeQuery = true)
    EstadisticasProjection obtenerEstadisticasOptimizadas(
        @Param("fecha") LocalDate fecha, 
        @Param("fechaLimite") LocalDate fechaLimite
    );
    
    /**
     * Projection para mapear el resultado de las estadísticas agregadas.
     */
    interface EstadisticasProjection {
        Integer getTotalActivos();
        Integer getTotalVencidos();
        Integer getVencenHoy();
        Integer getProximosVencimientos();
    }
}