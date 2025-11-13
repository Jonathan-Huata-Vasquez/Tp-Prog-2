package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.port.ICopiaRepository;
import biblioteca.biblioteca.web.dto.CopiaDto;
import biblioteca.biblioteca.web.mapper.CopiaDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ObtenerCopiaQueryHandler {

    private final ICopiaRepository copiaRepo;
    private final CopiaDtoMapper dtoMapper;

    @Transactional(readOnly = true)
    public CopiaDto handle(ObtenerCopiaQuery q) {
        if (q == null) throw new DatoInvalidoException("La query no puede ser null");
        var c = copiaRepo.porId(q.getIdCopia());
        if (c == null) throw new EntidadNoEncontradaException("Copia inexistente: " + q.getIdCopia());
        return dtoMapper.toDto(c);
    }
}
