package biblioteca.biblioteca.infrastructure.persistence.jpa.adapter;

import biblioteca.biblioteca.domain.model.Autor;
import biblioteca.biblioteca.domain.port.IAutorRepository;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.AutorEntity;
import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.AutorSpringDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AutorJpaAdapter implements IAutorRepository {

    private final AutorSpringDataRepository repo;
    private final AutorMapper mapper;

    @Override
    @Transactional
    public Autor guardar(Autor autor) {
        AutorEntity e = mapper.toEntity(autor);
        AutorEntity saved = repo.save(e); // INSERT si id=null; UPDATE si id!=null
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Autor porId(Integer idAutor) {
        return repo.findById(idAutor).map(mapper::toDomain).orElse(null);
    }

    @Override
    @Transactional
    public void eliminar(Integer idAutor) {
        repo.deleteById(idAutor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Autor> todos() {
        return repo.findAll().stream().map(mapper::toDomain).toList();
    }
}
