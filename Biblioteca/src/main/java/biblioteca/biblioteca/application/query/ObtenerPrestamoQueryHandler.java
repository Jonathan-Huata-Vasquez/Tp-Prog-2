package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.port.IPrestamoRepository;
import biblioteca.biblioteca.web.dto.PrestamoDto;
import biblioteca.biblioteca.web.mapper.PrestamoDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ObtenerPrestamoQueryHandler {

    private final IPrestamoRepository prestamoRepo;
    private final PrestamoDtoMapper dtoMapper;

    @Transactional(readOnly = true)
    public PrestamoDto handle(ObtenerPrestamoQuery q) {
        if (q == null) throw new DatoInvalidoException("La query no puede ser null");
        var p = prestamoRepo.porId(q.getIdPrestamo());
        if (p == null) throw new EntidadNoEncontradaException("Préstamo inexistente: " + q.getIdPrestamo());
        return dtoMapper.toDto(p);
    }
}
