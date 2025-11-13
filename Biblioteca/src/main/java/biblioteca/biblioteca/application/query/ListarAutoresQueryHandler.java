package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.domain.port.IAutorRepository;
import biblioteca.biblioteca.web.dto.AutorDto;
import biblioteca.biblioteca.web.mapper.AutorDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarAutoresQueryHandler {

    private final IAutorRepository autorRepo;
    private final AutorDtoMapper dtoMapper;

    @Transactional(readOnly = true)
    public List<AutorDto> handle(ListarAutoresQuery q) {
        return autorRepo.todos()
                .stream()
                .map(dtoMapper::toDto)
                .toList();
    }
}
