package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.port.IAutorRepository;
import biblioteca.biblioteca.web.dto.AutorDto;
import biblioteca.biblioteca.web.mapper.AutorDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ObtenerAutorQueryHandler {

    private final IAutorRepository autorRepo;
    private final AutorDtoMapper dtoMapper;

    @Transactional(readOnly = true)
    public AutorDto handle(ObtenerAutorQuery q) {
        if (q == null) throw new DatoInvalidoException("La query no puede ser null");
        var a = autorRepo.porId(q.getIdAutor());
        if (a == null) throw new EntidadNoEncontradaException("Autor inexistente: " + q.getIdAutor());
        return dtoMapper.toDto(a);
    }
}
