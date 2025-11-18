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
    private final biblioteca.biblioteca.domain.port.ILibroRepository libroRepo;

    @Transactional(readOnly = true)
    public List<CopiaDto> handle(ListarCopiasQuery q) {
        var listado = copiaRepo.todas();

        return listado.stream().map(copia -> {
            var dto = dtoMapper.toDto(copia);
            var libro = libroRepo.porId(copia.getIdLibro());
            String titulo = libro != null ? libro.getTitulo() : "";
            return CopiaDto.builder()
                    .id(dto.getId())
                    .idLibro(dto.getIdLibro())
                    .tituloLibro(titulo)
                    .estado(dto.getEstado())
                    .build();
        }).toList();
    }
}
