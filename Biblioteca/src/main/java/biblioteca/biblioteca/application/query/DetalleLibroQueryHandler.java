package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.model.Copia;
import biblioteca.biblioteca.domain.model.EstadoCopia;
import biblioteca.biblioteca.domain.model.Libro;
import biblioteca.biblioteca.domain.port.IAutorRepository;
import biblioteca.biblioteca.domain.port.ICopiaRepository;
import biblioteca.biblioteca.domain.port.IEditorialRepository;
import biblioteca.biblioteca.domain.port.ILibroRepository;
import biblioteca.biblioteca.domain.port.IPrestamoRepository;
import biblioteca.biblioteca.web.dto.CopiaDetalleDto;
import biblioteca.biblioteca.web.dto.LibroDetalleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DetalleLibroQueryHandler {

    private final ILibroRepository libroRepo;
    private final IAutorRepository autorRepo;
    private final IEditorialRepository editorialRepo;
    private final ICopiaRepository copiaRepo;
    private final IPrestamoRepository prestamoRepo;

    @Transactional(readOnly = true)
    public LibroDetalleDto handle(DetalleLibroQuery cmd) {
        if (cmd == null) throw new DatoInvalidoException("La query no puede ser null");
        
        Libro libro = libroRepo.porId(cmd.getIdLibro());
        if (libro == null) throw new EntidadNoEncontradaException("Libro inexistente: " + cmd.getIdLibro());
        
        var autor = autorRepo.porId(libro.getIdAutor());
        var editorial = editorialRepo.porId(libro.getIdEditorial());
        
        // Obtener todas las copias del libro
        List<Copia> copias = copiaRepo.porLibro(libro.getIdLibro());
        List<CopiaDetalleDto> copiasDto = copias.stream()
                .map(c -> CopiaDetalleDto.builder()
                        .id(c.getIdCopia())
                        .estado(mapearEstado(c.getEstado()))
                        .build())
                .collect(Collectors.toList());
        
        int total = copias.size();
        int disponibles = (int) copias.stream()
                .filter(c -> c.getEstado() == EstadoCopia.EnBiblioteca)
                .count();

        String desc = libro.getDescripcion() == null ? "" : libro.getDescripcion();
        String descripcionCompleta = desc.isEmpty() ? "Sin descripción disponible." : desc;

        // Verificar si el lector actual tiene este libro prestado
        Boolean prestadoAlLector = false;
        if (cmd.getIdLectorActual() != null) {
            // Verificar si el lector tiene alguna copia de este libro prestada
            List<Copia> copiasDelLibro = copiaRepo.porLibro(libro.getIdLibro());
            prestadoAlLector = copiasDelLibro.stream()
                    .anyMatch(copia -> prestamoRepo.activoPor(cmd.getIdLectorActual(), copia.getIdCopia()) != null);
        }

        return LibroDetalleDto.builder()
                .id(libro.getIdLibro())
                .titulo(libro.getTitulo())
                .anioPublicacion(libro.getAnioPublicacion())
                .categoria(libro.getCategoria().name())
                .autorNombre(autor.getNombre())
                .editorialNombre(editorial.getNombre())
                .descripcion(descripcionCompleta)
                .totalCopias(total)
                .copiasDisponibles(disponibles)
                .prestadoAlLector(prestadoAlLector)
                .copias(copiasDto)
                .build();
    }

    private String mapearEstado(EstadoCopia estado) {
        return switch (estado) {
            case EnBiblioteca -> "Disponible";
            case Prestada -> "Prestado";
            case EnReparacion -> "En reparación";
            case ConRetraso -> "Con retraso";
        };
    }
}