package biblioteca.biblioteca.infrastructure.persistence.jpa;

import biblioteca.biblioteca.application.query.IBibliotecaQueriesRepository;
import biblioteca.biblioteca.application.query.DashboardBibliotecarioCompleto;
import biblioteca.biblioteca.domain.model.*;
import biblioteca.biblioteca.domain.port.*;
import biblioteca.biblioteca.infrastructure.persistence.jpa.adapter.PrestamoMapper;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.PrestamoEntity;
import biblioteca.biblioteca.web.dto.BibliotecarioDashboardDto;
import biblioteca.biblioteca.web.dto.PrestamoDetalleDto;
import biblioteca.biblioteca.web.dto.AdminDashboardDto;
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

    @Override
    public List<biblioteca.biblioteca.web.dto.PrestamoBibliotecarioDto> obtenerTodosLosPrestamos(
            LocalDate fechaActual, int pagina, int tamanoPagina, String estadoFiltro) {
        
        int offset = pagina * tamanoPagina;
        List<Object[]> resultados;
        log.info("Ejecutando prestamos query: fechaActual={}, pagina={}, tamanoPagina={}, estadoFiltro={}, offset={}", fechaActual, pagina, tamanoPagina, estadoFiltro, offset);
        if (estadoFiltro == null || estadoFiltro.trim().isEmpty()) {
            log.info("Query: obtenerPrestamosPaginados(fechaActual={}, offset={}, tamanoPagina={})", fechaActual, offset, tamanoPagina);
            resultados = bibliotecaQueriesJpaRepo.obtenerPrestamosPaginados(
                fechaActual, offset, tamanoPagina);
        } else {
            log.info("Query: obtenerPrestamosPaginadosConFiltro(fechaActual={}, estadoFiltro={}, offset={}, tamanoPagina={})", fechaActual, estadoFiltro.toUpperCase(), offset, tamanoPagina);
            resultados = bibliotecaQueriesJpaRepo.obtenerPrestamosPaginadosConFiltro(
                fechaActual, estadoFiltro.toUpperCase(), offset, tamanoPagina);
        }
        return resultados.stream()
                .map(this::mapearResultadoAPrestamoBibliotecarioDto)
                .collect(Collectors.toList());
    }

    @Override
    public biblioteca.biblioteca.web.dto.ResumenPrestamosDto obtenerResumenPrestamos(LocalDate fechaActual) {
        List<Object[]> resultados = bibliotecaQueriesJpaRepo.obtenerResumenEstadisticas(fechaActual);
        Object[] resumen = resultados.get(0);
        
        int totalPrestamos = ((Number) resumen[0]).intValue();
        int prestamosActivos = ((Number) resumen[1]).intValue(); 
        int prestamosVencidos = ((Number) resumen[2]).intValue();
        int prestamosDevueltos = ((Number) resumen[3]).intValue();
        
        return biblioteca.biblioteca.web.dto.ResumenPrestamosDto.builder()
                .totalPrestamos(totalPrestamos)
                .prestamosActivos(prestamosActivos)
                .prestamosVencidos(prestamosVencidos)
                .prestamosDevueltos(prestamosDevueltos)
                .build();
    }

    @Override
    public AdminDashboardDto obtenerResumenAdmin() {
        List<Object[]> resultados = bibliotecaQueriesJpaRepo.obtenerResumenAdmin();
        Object[] r = resultados.get(0);
        int usuariosTotal = ((Number) r[0]).intValue();
        int librosTotal = ((Number) r[1]).intValue();
        int copiasTotal = ((Number) r[2]).intValue();
        int autoresTotal = ((Number) r[3]).intValue();
        int editorialesTotal = ((Number) r[4]).intValue();
        int usuariosAdmin = ((Number) r[5]).intValue();
        int usuariosBibliotecario = ((Number) r[6]).intValue();
        int usuariosLector = ((Number) r[7]).intValue();

        return AdminDashboardDto.builder()
                .usuariosTotal(usuariosTotal)
                .librosTotal(librosTotal)
                .copiasTotal(copiasTotal)
                .autoresTotal(autoresTotal)
                .editorialesTotal(editorialesTotal)
                .usuariosAdmin(usuariosAdmin)
                .usuariosBibliotecario(usuariosBibliotecario)
                .usuariosLector(usuariosLector)
                .build();
    }
    
    private biblioteca.biblioteca.web.dto.PrestamoBibliotecarioDto mapearResultadoAPrestamoBibliotecarioDto(Object[] resultado) {
        Number idPrestamo = (Number) resultado[0];
        Number idLector = (Number) resultado[1];
        String nombreLector = (String) resultado[2];
        Boolean lectorBloqueado = null;
        if (resultado[3] instanceof Number) {
            lectorBloqueado = ((Number) resultado[3]).intValue() == 1;
        } else if (resultado[3] instanceof Boolean) {
            lectorBloqueado = (Boolean) resultado[3];
        }
        String tituloLibro = (String) resultado[4];
        String autorNombre = (String) resultado[5];
        Number idCopia = (Number) resultado[6];
        java.sql.Date fechaInicioSql = (java.sql.Date) resultado[7];
        java.sql.Date fechaVencimientoSql = (java.sql.Date) resultado[8];
        java.sql.Date fechaDevolucionSql = (java.sql.Date) resultado[9];
        Number diasAtraso = (Number) resultado[10];
        
        LocalDate fechaInicio = fechaInicioSql.toLocalDate();
        LocalDate fechaVencimiento = fechaVencimientoSql.toLocalDate();
        LocalDate fechaDevolucion = fechaDevolucionSql != null ? fechaDevolucionSql.toLocalDate() : null;
        
        String estado;
        if (fechaDevolucion != null) {
            estado = "DEVUELTO";
        } else if (fechaVencimiento.isBefore(LocalDate.now())) {
            estado = "VENCIDO";
        } else {
            estado = "ACTIVO";
        }
        
        return biblioteca.biblioteca.web.dto.PrestamoBibliotecarioDto.builder()
                .idPrestamo(idPrestamo.intValue())
                .idLector(idLector.intValue())
                .nombreLector(nombreLector)
                .lectorBloqueado(Boolean.TRUE.equals(lectorBloqueado))
                .tituloLibro(tituloLibro)
                .autorNombre(autorNombre)
                .idCopia(idCopia.intValue())
                .fechaInicio(fechaInicio)
                .fechaVencimiento(fechaVencimiento)
                .fechaDevolucion(fechaDevolucion)
                .estado(estado)
                .diasAtraso(diasAtraso != null ? diasAtraso.intValue() : 0)
                .build();
    }
}