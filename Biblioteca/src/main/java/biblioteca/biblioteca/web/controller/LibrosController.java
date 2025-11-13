package biblioteca.biblioteca.web.controller;

import biblioteca.biblioteca.application.command.*;
import biblioteca.biblioteca.application.query.ListarLibrosQuery;
import biblioteca.biblioteca.application.query.ListarLibrosQueryHandler;
import biblioteca.biblioteca.application.query.ObtenerLibroQuery;
import biblioteca.biblioteca.application.query.ObtenerLibroQueryHandler;
import biblioteca.biblioteca.web.dto.LibroDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/libros")
@RequiredArgsConstructor
public class LibrosController {

    private final CrearLibroCommandHandler crearHandler;
    private final ActualizarLibroCommandHandler actualizarHandler;
    private final EliminarLibroCommandHandler eliminarHandler;
    private final ObtenerLibroQueryHandler obtenerHandler;
    private final ListarLibrosQueryHandler listarHandler;

    @PostMapping
    public ResponseEntity<LibroDto> crear(@Valid @RequestBody CrearLibroCommand body) {
        var dto = crearHandler.handle(body);
        return ResponseEntity
                .created(URI.create("/api/libros/" + dto.getId()))
                .body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LibroDto> actualizar(@PathVariable Integer id,
                                               @Valid @RequestBody ActualizarLibroCommand body) {
        var dto = actualizarHandler.handle(
                ActualizarLibroCommand.builder()
                        .idLibro(id) // el path domina al body
                        .titulo(body.getTitulo())
                        .anioPublicacion(body.getAnioPublicacion())
                        .idAutor(body.getIdAutor())
                        .idEditorial(body.getIdEditorial())
                        .categoria(body.getCategoria())
                        .build()
        );
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        eliminarHandler.handle(EliminarLibroCommand.builder().idLibro(id).build());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibroDto> porId(@PathVariable Integer id) {
        var dto = obtenerHandler.handle(ObtenerLibroQuery.builder().idLibro(id).build());
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<LibroDto>> todos() {
        var lista = listarHandler.handle(ListarLibrosQuery.builder().build());
        return ResponseEntity.ok(lista);
    }
}
