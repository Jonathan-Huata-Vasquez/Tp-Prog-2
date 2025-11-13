package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.port.ILibroRepository;
import biblioteca.biblioteca.web.dto.LibroDto;
import biblioteca.biblioteca.web.mapper.LibroDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ObtenerLibroQueryHandler {

    private final ILibroRepository libroRepo;
    private final LibroDtoMapper dtoMapper;

    @Transactional(readOnly = true)
    public LibroDto handle(ObtenerLibroQuery q) {
        if (q == null) throw new DatoInvalidoException("La query no puede ser null");
        var l = libroRepo.porId(q.getIdLibro());
        if (l == null) throw new EntidadNoEncontradaException("Libro inexistente: " + q.getIdLibro());
        return dtoMapper.toDto(l);
    }
}
