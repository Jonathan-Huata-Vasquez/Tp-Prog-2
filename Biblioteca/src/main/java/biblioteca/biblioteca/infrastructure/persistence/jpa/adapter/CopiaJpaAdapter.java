package biblioteca.biblioteca.infrastructure.persistence.jpa.adapter;

import biblioteca.biblioteca.domain.model.Copia;
import biblioteca.biblioteca.domain.model.EstadoCopia;
import biblioteca.biblioteca.domain.port.ICopiaRepository;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.CopiaEntity;
import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.CopiaSpringDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CopiaJpaAdapter implements ICopiaRepository {

    private final CopiaSpringDataRepository repo;
    private final CopiaMapper mapper;

    @Override
    @Transactional
    public Copia guardar(Copia copia) {
        CopiaEntity e = mapper.toEntity(copia);
        CopiaEntity saved = repo.save(e);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Copia porId(Integer idCopia) {
        return repo.findById(idCopia).map(mapper::toDomain).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Copia> disponiblesPorLibro(Integer idLibro) {
        return repo.findByLibroIdAndEstado(idLibro, EstadoCopia.EnBiblioteca)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public void eliminar(Integer idCopia) {
        repo.deleteById(idCopia);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Copia> todas() {
        return repo.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Copia> porLibro(Integer idLibro) {
        return repo.findByLibroId(idLibro).stream().map(mapper::toDomain).toList();
    }

    @Override
    public int contarPorLibro(Integer idLibro) {
        return repo.countByLibroId(idLibro);
    }

    @Override
    public int contarPorLibroYEstado(Integer idLibro, EstadoCopia estado) {
        return repo.countByLibroIdAndEstado(idLibro, estado);
    }
}