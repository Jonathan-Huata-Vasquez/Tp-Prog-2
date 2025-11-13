package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.domain.port.IEditorialRepository;
import biblioteca.biblioteca.web.dto.EditorialDto;
import biblioteca.biblioteca.web.mapper.EditorialDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarEditorialesQueryHandler {

    private final IEditorialRepository editorialRepo;
    private final EditorialDtoMapper dtoMapper;

    @Transactional(readOnly = true)
    public List<EditorialDto> handle(ListarEditorialesQuery q) {
        return editorialRepo.todas()
                .stream()
                .map(dtoMapper::toDto)
                .toList();
    }
}
