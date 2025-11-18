package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.port.IEditorialRepository;
import biblioteca.biblioteca.domain.port.ILibroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EliminarEditorialCommandHandler {

    private final IEditorialRepository editorialRepo;
    private final ILibroRepository  libroRepo;

    @Transactional
    public void handle(EliminarEditorialCommand cmd) {
        if (cmd == null) throw new DatoInvalidoException("El comando no puede ser null");
        var existente = editorialRepo.porId(cmd.getIdEditorial());
        if (existente == null) throw new EntidadNoEncontradaException("Editorial inexistente: " + cmd.getIdEditorial());

        var tieneLibros = libroRepo.contarEditorialPorLibro(cmd.getIdEditorial()) > 0;
        if (tieneLibros) { throw new DatoInvalidoException ("Ne se puedo borrar editorial porque tiene libros");  }
        editorialRepo.eliminar(cmd.getIdEditorial());
    }
}
