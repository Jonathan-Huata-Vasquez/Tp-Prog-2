package biblioteca.biblioteca.infrastructure.persistence.jpa.adapter;

import biblioteca.biblioteca.domain.model.Editorial;
import biblioteca.biblioteca.domain.port.IEditorialRepository;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.EditorialEntity;
import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.EditorialSpringDataRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EditorialJpaAdapter implements IEditorialRepository {

    private final EditorialSpringDataRepository repo;
    private final EditorialMapper mapper;

    @Override
    @Transactional
    public Editorial guardar(Editorial editorial) {
        EditorialEntity e = mapper.toEntity(editorial);
        EditorialEntity saved = repo.save(e); // INSERT si id=null; UPDATE si id!=null
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Editorial porId(Integer idEditorial) {
        return repo.findById(idEditorial).map(mapper::toDomain).orElse(null);
    }

    @Override
    @Transactional
    public void eliminar(Integer idEditorial) {
        repo.deleteById(idEditorial);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Editorial> todas() {
        return repo.findAll().stream().map(mapper::toDomain).toList();
    }
}

