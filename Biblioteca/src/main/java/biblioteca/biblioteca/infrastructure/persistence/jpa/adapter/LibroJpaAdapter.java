package biblioteca.biblioteca.infrastructure.persistence.jpa.adapter;

import biblioteca.biblioteca.domain.model.Libro;
import biblioteca.biblioteca.domain.port.ILibroRepository;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.LibroEntity;
import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.CopiaSpringDataRepository;
import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.LibroSpringDataRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LibroJpaAdapter implements ILibroRepository {

    private final LibroSpringDataRepository repo;
    private final LibroMapper mapper;
    private final CopiaSpringDataRepository copiaRepo;

    @Override
    @Transactional
    public Libro guardar(Libro libro) {
        LibroEntity e = mapper.toEntity(libro);
        LibroEntity saved = repo.save(e);            // INSERT si id=null; UPDATE si id!=null
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Libro porId(Integer idLibro) {
        return repo.findById(idLibro).map(mapper::toDomain).orElse(null);
    }


    @Override
    @Transactional
    public void eliminar(Integer idLibro) {
        repo.deleteById(idLibro);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Libro> todos() {
        return repo.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Libro> buscarPorTitulo(String q) {
        return repo.findByTituloContainingIgnoreCase(q)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public int contarCopiasPorLibro(Integer idLibro) {
        return copiaRepo.countByLibroId(idLibro);
    }
}
