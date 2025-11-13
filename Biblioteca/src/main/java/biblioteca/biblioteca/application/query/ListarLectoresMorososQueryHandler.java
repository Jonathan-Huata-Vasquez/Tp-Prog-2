package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.port.ILectorRepository;
import biblioteca.biblioteca.web.dto.LectorDto;
import biblioteca.biblioteca.web.mapper.LectorDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarLectoresMorososQueryHandler {

    private final ILectorRepository lectorRepo;
    private final LectorDtoMapper dtoMapper;

    @Transactional(readOnly = true)
    public List<LectorDto> handle(ListarLectoresMorososQuery q) {
        if (q == null) throw new DatoInvalidoException("La query no puede ser null");
        return lectorRepo.lectoresBloqueados(q.getHastaFecha())
                .stream()
                .map(dtoMapper::toDto)
                .toList();
    }
}
