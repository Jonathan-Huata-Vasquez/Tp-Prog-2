package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.LectorSpringDataRepository;
import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.PrestamoSpringDataRepository;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.LectorEntity;
import biblioteca.biblioteca.web.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handler para consulta de lectores usando repositorios JPA reales.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ListarLectoresQueryHandler {

    private final LectorSpringDataRepository lectorRepository;
    private final PrestamoSpringDataRepository prestamoRepository;

    public ListarLectoresResultDto handle(ListarLectoresQuery query) {
        log.debug("Ejecutando query de lectores con filtro: {}", query.getEstadoFiltro());

        try {
            LocalDate hoy = LocalDate.now();
            
            // Obtener lectores con paginación según filtro
            Pageable pageable = PageRequest.of(query.getPagina(), query.getTamano(), Sort.by("id"));
            Page<LectorEntity> paginaEntities;
            
            // Usar consultas específicas para cada filtro
            if ("BLOQUEADO".equals(query.getEstadoFiltro())) {
                paginaEntities = lectorRepository.findBloqueados(hoy, pageable);
            } else if ("HABILITADO".equals(query.getEstadoFiltro())) {
                paginaEntities = lectorRepository.findHabilitados(hoy, pageable);
            } else {
                paginaEntities = lectorRepository.findAll(pageable);
            }

            // Convertir entities a DTOs
            List<LectorBibliotecarioDto> lectoresDto = paginaEntities.getContent().stream()
                    .map(entity -> convertirADto(entity, hoy))
                    .collect(Collectors.toList());

            // Crear página de DTOs
            PaginaDto<LectorBibliotecarioDto> paginaLectores = PaginaDto.of(
                    lectoresDto,
                    query.getPagina(),
                    query.getTamano(),
                    (int) paginaEntities.getTotalElements()
            );

            // Crear resumen real
            ResumenLectoresDto resumen = crearResumenReal(hoy);

            return ListarLectoresResultDto.builder()
                    .paginaLectores(paginaLectores)
                    .resumen(resumen)
                    .build();

        } catch (Exception e) {
            log.error("Error al procesar query de lectores", e);
            throw new RuntimeException("Error al obtener lectores", e);
        }
    }

    private LectorBibliotecarioDto convertirADto(LectorEntity entity, LocalDate hoy) {
        boolean estaBloqueado = entity.getBloqueadoHasta() != null && !hoy.isAfter(entity.getBloqueadoHasta());
        
        // Obtener estadísticas reales de préstamos
        int prestamosActivos = prestamoRepository.countByLectorIdAndFechaDevolucionIsNull(entity.getId());
        int prestamosVencidos = prestamoRepository.countByLectorIdAndFechaDevolucionIsNullAndFechaVencimientoBefore(entity.getId(), hoy);
        int totalDevueltos = prestamoRepository.countByLectorIdAndFechaDevolucionIsNotNull(entity.getId());
        
        // Calcular días de bloqueo restantes
        int diasBloqueo = 0;
        if (estaBloqueado) {
            diasBloqueo = (int) hoy.until(entity.getBloqueadoHasta()).getDays();
        }

        return LectorBibliotecarioDto.builder()
                .idLector(entity.getId())
                .nombreCompleto(entity.getNombre())
                .bloqueado(estaBloqueado)
                .prestamosActivos(prestamosActivos)
                .prestamosVencidos(prestamosVencidos)
                .totalDevueltos(totalDevueltos)
                .diasBloqueo(diasBloqueo)
                .build();
    }

    private ResumenLectoresDto crearResumenReal(LocalDate hoy) {
        // Contar todos los lectores
        long totalLectores = lectorRepository.count();
        
        // Contar lectores bloqueados usando consulta optimizada
        long bloqueados = lectorRepository.countBloqueados(hoy);
        int habilitados = (int) (totalLectores - bloqueados);
        
        return ResumenLectoresDto.builder()
                .totalLectores((int) totalLectores)
                .habilitados(habilitados)
                .bloqueados((int) bloqueados)
                .build();
    }
}