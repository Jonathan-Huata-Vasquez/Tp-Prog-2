package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.domain.port.ICopiaRepository;
import biblioteca.biblioteca.web.dto.CopiaDto;
import biblioteca.biblioteca.web.mapper.CopiaDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarCopiasQueryHandler {

    private final ICopiaRepository copiaRepo;
    private final CopiaDtoMapper dtoMapper;

    @Transactional(readOnly = true)
    public List<CopiaDto> handle(ListarCopiasQuery q) {
        var listado = (q != null && q.getIdLibro() != null)
                ? copiaRepo.porLibro(q.getIdLibro())
                : copiaRepo.todas();
        return listado.stream().map(dtoMapper::toDto).toList();
    }
}
