package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.port.IPrestamoRepository;
import biblioteca.biblioteca.web.dto.PrestamoDto;
import biblioteca.biblioteca.web.mapper.PrestamoDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrestamosActivosDeQueryHandler {

    private final IPrestamoRepository prestamoRepo;
    private final PrestamoDtoMapper dtoMapper;

    @Transactional(readOnly = true)
    public List<PrestamoDto> handle(PrestamosActivosDeQuery q) {
        if (q == null) throw new DatoInvalidoException("La query no puede ser null");
        return prestamoRepo.activosPorLector(q.getIdLector())
                .stream()
                .map(dtoMapper::toDto)
                .toList();
    }
}
