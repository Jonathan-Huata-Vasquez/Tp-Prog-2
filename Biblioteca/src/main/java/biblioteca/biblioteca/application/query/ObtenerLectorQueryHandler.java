package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.port.ILectorRepository;
import biblioteca.biblioteca.web.dto.LectorDto;
import biblioteca.biblioteca.web.mapper.LectorDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ObtenerLectorQueryHandler {

    private final ILectorRepository lectorRepo;
    private final LectorDtoMapper dtoMapper;

    @Transactional(readOnly = true)
    public LectorDto handle(ObtenerLectorQuery q) {
        if (q == null) throw new DatoInvalidoException("La query no puede ser null");
        var l = lectorRepo.porId(q.getIdLector());
        if (l == null) throw new EntidadNoEncontradaException("Lector inexistente: " + q.getIdLector());
        return dtoMapper.toDto(l);
    }
}
