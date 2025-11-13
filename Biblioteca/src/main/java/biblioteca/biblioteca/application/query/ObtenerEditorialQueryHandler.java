package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.port.IEditorialRepository;
import biblioteca.biblioteca.web.dto.EditorialDto;
import biblioteca.biblioteca.web.mapper.EditorialDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ObtenerEditorialQueryHandler {

    private final IEditorialRepository editorialRepo;
    private final EditorialDtoMapper dtoMapper;

    @Transactional(readOnly = true)
    public EditorialDto handle(ObtenerEditorialQuery q) {
        if (q == null) throw new DatoInvalidoException("La query no puede ser null");
        var e = editorialRepo.porId(q.getIdEditorial());
        if (e == null) throw new EntidadNoEncontradaException("Editorial inexistente: " + q.getIdEditorial());
        return dtoMapper.toDto(e);
    }
}