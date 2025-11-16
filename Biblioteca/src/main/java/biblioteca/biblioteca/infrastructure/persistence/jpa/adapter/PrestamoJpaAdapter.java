package biblioteca.biblioteca.infrastructure.persistence.jpa.adapter;


import biblioteca.biblioteca.domain.model.Prestamo;
import biblioteca.biblioteca.domain.port.IPrestamoRepository;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.PrestamoEntity;
import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.PrestamoSpringDataRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class PrestamoJpaAdapter implements IPrestamoRepository {

    private final PrestamoSpringDataRepository repo;
    private final PrestamoMapper mapper;

    public PrestamoJpaAdapter(PrestamoSpringDataRepository repo, PrestamoMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Prestamo guardar(Prestamo prestamo) {
        var entity = mapper.toEntity(prestamo);
        var saved  = repo.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Prestamo porId(Integer idPrestamo) {
        return repo.findById(idPrestamo).map(mapper::toDomain).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Prestamo activoPor(Integer idLector, Integer idCopia) {
        return repo.findByLectorIdAndCopiaIdAndFechaDevolucionIsNull(idLector, idCopia)
                .map(mapper::toDomain)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prestamo> activosPorLector(Integer idLector) {
        return repo.findByLectorIdAndFechaDevolucionIsNull(idLector)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prestamo> todosLosPorLector(Integer idLector) {
        return repo.findByLectorId(idLector)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Prestamo> todosLosPorLectorOrdenadosPorVencimiento(Integer idLector) {
        return repo.findByLectorIdOrderByFechaDevolucionNullsFirstFechaVencimientoAsc(idLector)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public int contarActivosPorLector(Integer idLector) {
        return repo.countByLectorIdAndFechaDevolucionIsNull(idLector);
    }
    
    @Override
    @Transactional(readOnly = true)
    public int contarVencidosPorLector(Integer idLector) {
        return repo.countByLectorIdAndFechaDevolucionIsNullAndFechaVencimientoBefore(idLector, LocalDate.now());
    }
    
    @Override
    @Transactional(readOnly = true)
    public int contarDevueltosPorLector(Integer idLector) {
        return repo.countByLectorIdAndFechaDevolucionIsNotNull(idLector);
    }
}
