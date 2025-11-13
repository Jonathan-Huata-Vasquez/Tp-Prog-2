package biblioteca.biblioteca.infrastructure.persistence.jpa.adapter;


import biblioteca.biblioteca.domain.model.Lector;
import biblioteca.biblioteca.domain.model.Prestamo;
import biblioteca.biblioteca.domain.port.ILectorRepository;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.LectorEntity;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.PrestamoEntity;
import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.LectorSpringDataRepository;
import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.PrestamoSpringDataRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class LectorJpaAdapter implements ILectorRepository {

    private final LectorSpringDataRepository lectorRepo;
    private final PrestamoSpringDataRepository prestamoRepo;
    private final LectorMapper lectorMapper;
    private final PrestamoMapper prestamoMapper;

    public LectorJpaAdapter(LectorSpringDataRepository lectorRepo,
                            PrestamoSpringDataRepository prestamoRepo,
                            LectorMapper lectorMapper,
                            PrestamoMapper prestamoMapper) {
        this.lectorRepo = lectorRepo;
        this.prestamoRepo = prestamoRepo;
        this.lectorMapper = lectorMapper;
        this.prestamoMapper = prestamoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Lector porId(Integer idLector) {
        return lectorRepo.findById(idLector)
                .map(e -> {
                    List<PrestamoEntity> activos = prestamoRepo.findByLectorIdAndFechaDevolucionIsNull(e.getId());
                    List<Prestamo> domActivos = activos.stream().map(prestamoMapper::toDomain).toList();
                    return lectorMapper.toDomainWithPrestamos(e, domActivos);
                })
                .orElse(null);
    }

    @Override
    @Transactional
    public void guardar(Lector lector) {
        LectorEntity e = lectorMapper.toEntity(lector);
        lectorRepo.save(e);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lector> lectoresBloqueados(LocalDate hastaFecha) {
        return lectorRepo.bloqueadosDesde(hastaFecha).stream()
                .map(lectorMapper::toDomain) // para listados no necesito préstamos activos
                .toList();
    }
}
