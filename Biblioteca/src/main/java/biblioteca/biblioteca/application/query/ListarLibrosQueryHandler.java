package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.domain.port.ILibroRepository;
import biblioteca.biblioteca.web.dto.LibroDto;
import biblioteca.biblioteca.web.mapper.LibroDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarLibrosQueryHandler {

    private final ILibroRepository libroRepo;
    private final LibroDtoMapper dtoMapper;

    @Transactional(readOnly = true)
    public List<LibroDto> handle(ListarLibrosQuery q) {
        return libroRepo.todos()
                .stream()
                .map(dtoMapper::toDto)
                .toList();
    }
}
