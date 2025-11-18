package biblioteca.biblioteca.application.query;


import biblioteca.biblioteca.domain.model.EstadoCopia;
import biblioteca.biblioteca.domain.model.Libro;
import biblioteca.biblioteca.domain.port.IAutorRepository;
import biblioteca.biblioteca.domain.port.ICopiaRepository;
import biblioteca.biblioteca.domain.port.IEditorialRepository;
import biblioteca.biblioteca.domain.port.ILibroRepository;
import biblioteca.biblioteca.web.dto.CatalogoResultDto;
import biblioteca.biblioteca.web.dto.LibroCatalogoItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarCatalogoQueryHandler {

    private final ILibroRepository libroRepo;
    private final IAutorRepository autorRepo;
    private final IEditorialRepository editorialRepo;
    private final ICopiaRepository copiaRepo;

    public CatalogoResultDto handle(ListarCatalogoQuery cmd) {
        final String q = (cmd.getQ() == null) ? "" : cmd.getQ().trim();

        List<Libro> libros = q.isEmpty()
                ? libroRepo.todos()
                : libroRepo.buscarPorTitulo(q);

        List<LibroCatalogoItemDto> items = libros.stream()
                .map(l -> {
                    var autor = autorRepo.porId(l.getIdAutor());
                    var editorial = editorialRepo.porId(l.getIdEditorial());

                    int total = copiaRepo.contarPorLibro(l.getIdLibro());
                    int disponibles = copiaRepo.contarPorLibroYEstado(l.getIdLibro(), EstadoCopia.EnBiblioteca);

                    // descripcionCorta: ej. primeros 180 caracteres
                    String desc = l.getDescripcion() == null ? "" : l.getDescripcion();
                    String corta = desc.length() <= 180 ? desc : (desc.substring(0, 180) + "…");

                    // String portada = portadaService.urlDePortada(l.getIdLibro());
                    String portada = null;
                    return LibroCatalogoItemDto.builder()
                            .id(l.getIdLibro())
                            .titulo(l.getTitulo())
                            .anioPublicacion(l.getAnioPublicacion())
                            .categoria(l.getCategoria().name())
                            .autorNombre(autor.getNombre())
                            .editorialNombre(editorial.getNombre())
                            .descripcionCorta(corta)
                            .portadaUrl(portada) // si es null, la vista usa la portada “placeholder”
                            .totalCopias(total)
                            .copiasDisponibles(disponibles)
                            .build();
                })
                .toList();

        return CatalogoResultDto.builder()
                .criterio(q.isEmpty() ? null : q)
                .totalLibros(items.size())
                .items(items)
                .build();
    }
}
