package biblioteca.biblioteca.infrastructure.persistence.jpa;

import biblioteca.biblioteca.application.query.IBibliotecaQueriesRepository;
import biblioteca.biblioteca.application.query.DashboardBibliotecarioCompleto;
import biblioteca.biblioteca.domain.model.*;
import biblioteca.biblioteca.domain.port.*;
import biblioteca.biblioteca.infrastructure.persistence.jpa.adapter.PrestamoMapper;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.PrestamoEntity;
import biblioteca.biblioteca.web.dto.BibliotecarioDashboardDto;
import biblioteca.biblioteca.web.dto.PrestamoDetalleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BibliotecaQueriesRepositoryImpl implements IBibliotecaQueriesRepository {

    private final BibliotecaQueriesJpaRepository bibliotecaQueriesJpaRepo;
    private final ICopiaRepository copiaRepo;
    private final ILibroRepository libroRepo;
    private final IAutorRepository autorRepo;
    private final PrestamoMapper  prestamoMapper;

    
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    @Override
    public DashboardBibliotecarioCompleto obtenerDashboardCompleto(
            LocalDate fecha, int diasProximoVencimiento, int limitePrestamosDestacados) {
        
        log.debug("Ejecutando pseudo-queries optimizadas para dashboard completo del bibliotecario");
        
        // PSEUDO-QUERY 1: Obtener estadísticas con consulta nativa optimizada
        LocalDate fechaLimite = fecha.plusDays(diasProximoVencimiento);
        BibliotecaQueriesJpaRepository.EstadisticasProjection estadisticas = 
            bibliotecaQueriesJpaRepo.obtenerEstadisticasOptimizadas(fecha, fechaLimite);
        
        // PSEUDO-QUERY 2: Obtener préstamos destacados con consulta nativa optimizada
        List<PrestamoEntity> prestamosEntity = bibliotecaQueriesJpaRepo
            .findPrestamosDestacadosOptimizado(limitePrestamosDestacados);
        
        // Convertir a DTOs con mapeo manual optimizado
        List<PrestamoDetalleDto> prestamosDestacadosDto = prestamosEntity.stream()
            .map(this::mapearAPrestamoDetalleDto)
            .collect(Collectors.toList());
        
        // Construir DTO completo
        BibliotecarioDashboardDto dashboard = BibliotecarioDashboardDto.builder()
            .cantidadActivos(estadisticas.getTotalActivos())
            .cantidadVencidos(estadisticas.getTotalVencidos())
            .cantidadHoy(estadisticas.getVencenHoy())
            .proximosVencimientos(estadisticas.getProximosVencimientos())
            .prestamosDestacados(prestamosDestacadosDto)
            .build();
        
        log.debug("Dashboard completo generado con pseudo-queries nativas: {} activos, {} vencidos, {} destacados", 
                  estadisticas.getTotalActivos(), estadisticas.getTotalVencidos(), prestamosDestacadosDto.size());
        
        return DashboardBibliotecarioCompleto.builder()
            .dashboard(dashboard)
            .build();
    }
    
    /**
     * Mapeo optimizado de PrestamoEntity a PrestamoDetalleDto para el dashboard.
     * Solo obtiene la información mínima necesaria para mostrar en la tabla.
     */
    private PrestamoDetalleDto mapearAPrestamoDetalleDto(PrestamoEntity entity) {
        // Convertir entity a domain model
        Prestamo prestamo = prestamoMapper.toDomain(entity);
        
        // Obtener información básica de la copia y libro
        Copia copia = copiaRepo.porId(prestamo.getIdCopia());
        Libro libro = libroRepo.porId(copia.getIdLibro());
        Autor autor = autorRepo.porId(libro.getIdAutor());
        
        // Determinar estado básico
        String estado = determinarEstado(prestamo).name();
        int diasAtraso = prestamo.diasAtrasoAl(LocalDate.now());
        
        return PrestamoDetalleDto.builder()
            .codigo(prestamo.getIdPrestamo().toString())
            .estado(estado)
            .diasAtraso(diasAtraso)
            .fechaPrestamo(prestamo.getFechaInicio().format(FORMATO_FECHA))
            .fechaVencimiento(prestamo.getFechaVencimiento().format(FORMATO_FECHA))
            .fechaDevolucion(prestamo.getFechaDevolucion() != null ? 
                prestamo.getFechaDevolucion().format(FORMATO_FECHA) : null)
            .tituloLibro(libro.getTitulo())
            .autor(autor.getNombre())
            .editorial(null) // No necesario para el dashboard
            .anioPublicacion(null) // No necesario para el dashboard  
            .urlPortada(null)
            .idCopia(prestamo.getIdCopia())
            .idLector(prestamo.getIdLector())
            .nombreLector(null) // No necesario para el dashboard
            .build();
    }
    
    private EstadoPrestamo determinarEstado(Prestamo prestamo) {
        if (prestamo.getFechaDevolucion() != null) {
            return EstadoPrestamo.DEVUELTO;
        }
        if (prestamo.getFechaVencimiento().isBefore(LocalDate.now())) {
            return EstadoPrestamo.VENCIDO;
        }
        return EstadoPrestamo.ACTIVO;
    }
}