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

    /**
     * Query nativa optimizada para obtener préstamos paginados con JOIN.
     * Incluye información completa de lector, libro y autor en una sola consulta.
     */
    @Query(value = """
        SELECT 
            p.id,
            p.lector_id, 
            l.nombre as nombre_lector,
            CASE WHEN l.bloqueado_hasta IS NOT NULL AND l.bloqueado_hasta >= CURRENT_DATE THEN true ELSE false END as bloqueado,
            lib.titulo as titulo_libro,
            a.nombre as autor_nombre,
            p.copia_id,
            p.fecha_inicio,
            p.fecha_vencimiento,
            p.fecha_devolucion,
            CASE 
                WHEN p.fecha_devolucion IS NULL AND p.fecha_vencimiento < :fechaActual 
                THEN DATEDIFF('DAY', p.fecha_vencimiento, :fechaActual)
                ELSE 0
            END as dias_atraso
        FROM prestamo p
        INNER JOIN lector l ON p.lector_id = l.id
        INNER JOIN copia c ON p.copia_id = c.id  
        INNER JOIN libro lib ON c.libro_id = lib.id
        INNER JOIN autor a ON lib.autor_id = a.id
        ORDER BY p.fecha_inicio DESC, p.id DESC
        LIMIT :limite OFFSET :offset
        """, nativeQuery = true)
    List<Object[]> obtenerPrestamosPaginados(
        @Param("fechaActual") LocalDate fechaActual,
        @Param("offset") int offset,
        @Param("limite") int limite
    );

    /**
     * Query nativa optimizada para préstamos paginados con filtro de estado.
     */
    @Query(value = """
        SELECT 
            p.id,
            p.lector_id, 
            l.nombre as nombre_lector,
            CASE WHEN l.bloqueado_hasta IS NOT NULL AND l.bloqueado_hasta >= CURRENT_DATE THEN true ELSE false END as bloqueado,
            lib.titulo as titulo_libro,
            a.nombre as autor_nombre,
            p.copia_id,
            p.fecha_inicio,
            p.fecha_vencimiento,
            p.fecha_devolucion,
            CASE 
                WHEN p.fecha_devolucion IS NULL AND p.fecha_vencimiento < :fechaActual 
                THEN DATEDIFF('DAY', p.fecha_vencimiento, :fechaActual)
                ELSE 0
            END as dias_atraso
        FROM prestamo p
        INNER JOIN lector l ON p.lector_id = l.id
        INNER JOIN copia c ON p.copia_id = c.id  
        INNER JOIN libro lib ON c.libro_id = lib.id
        INNER JOIN autor a ON lib.autor_id = a.id
        WHERE 
            CASE 
                WHEN :estadoFiltro = 'ACTIVO' THEN p.fecha_devolucion IS NULL AND p.fecha_vencimiento >= :fechaActual
                WHEN :estadoFiltro = 'VENCIDO' THEN p.fecha_devolucion IS NULL AND p.fecha_vencimiento < :fechaActual
                WHEN :estadoFiltro = 'DEVUELTO' THEN p.fecha_devolucion IS NOT NULL
                ELSE 1=1
            END
        ORDER BY p.fecha_inicio DESC, p.id DESC
        LIMIT :limite OFFSET :offset
        """, nativeQuery = true)
    List<Object[]> obtenerPrestamosPaginadosConFiltro(
        @Param("fechaActual") LocalDate fechaActual,
        @Param("estadoFiltro") String estadoFiltro,
        @Param("offset") int offset,
        @Param("limite") int limite
    );

    /**
     * Query nativa para obtener resumen estadístico de préstamos.
     * Optimizada para calcular todos los contadores en una sola consulta.
     */
    @Query(value = """
        SELECT 
            COUNT(*) as total_prestamos,
            COUNT(CASE WHEN p.fecha_devolucion IS NULL AND p.fecha_vencimiento >= :fechaActual THEN 1 END) as prestamos_activos,
            COUNT(CASE WHEN p.fecha_devolucion IS NULL AND p.fecha_vencimiento < :fechaActual THEN 1 END) as prestamos_vencidos,
            COUNT(CASE WHEN p.fecha_devolucion IS NOT NULL THEN 1 END) as prestamos_devueltos
        FROM prestamo p
        """, nativeQuery = true)
    List<Object[]> obtenerResumenEstadisticas(@Param("fechaActual") LocalDate fechaActual);

    // Estadísticas agregadas para dashboard administrador (totales y desglose por rol)
    @Query(value = """
        SELECT 
            (SELECT COUNT(*) FROM usuario) AS usuarios_total,
            (SELECT COUNT(*) FROM libro) AS libros_total,
            (SELECT COUNT(*) FROM copia) AS copias_total,
            (SELECT COUNT(*) FROM autor) AS autores_total,
            (SELECT COUNT(*) FROM editorial) AS editoriales_total,
            (SELECT COUNT(*) FROM usuario_rol ur INNER JOIN rol r ON ur.rol_id = r.id WHERE r.nombre = 'ADMINISTRADOR') AS usuarios_admin,
            (SELECT COUNT(*) FROM usuario_rol ur INNER JOIN rol r ON ur.rol_id = r.id WHERE r.nombre = 'BIBLIOTECARIO') AS usuarios_bibliotecario,
            (SELECT COUNT(*) FROM usuario_rol ur INNER JOIN rol r ON ur.rol_id = r.id WHERE r.nombre = 'LECTOR') AS usuarios_lector
        """, nativeQuery = true)
    List<Object[]> obtenerResumenAdmin();
}