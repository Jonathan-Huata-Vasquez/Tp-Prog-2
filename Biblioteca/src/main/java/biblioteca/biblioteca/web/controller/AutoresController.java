package biblioteca.biblioteca.web.controller;

import biblioteca.biblioteca.application.command.*;
import biblioteca.biblioteca.application.query.ListarAutoresQuery;
import biblioteca.biblioteca.application.query.ListarAutoresQueryHandler;
import biblioteca.biblioteca.application.query.ObtenerAutorQuery;
import biblioteca.biblioteca.application.query.ObtenerAutorQueryHandler;
import biblioteca.biblioteca.web.dto.AutorDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/autores")
@RequiredArgsConstructor
public class AutoresController {

    private final CrearAutorCommandHandler crearHandler;
    private final ActualizarAutorCommandHandler actualizarHandler;
    private final EliminarAutorCommandHandler eliminarHandler;
    private final ObtenerAutorQueryHandler obtenerHandler;
    private final ListarAutoresQueryHandler listarHandler;

    @PostMapping
    public ResponseEntity<AutorDto> crear(@Valid @RequestBody CrearAutorCommand body) {
        var dto = crearHandler.handle(body);
        return ResponseEntity
                .created(URI.create("/api/autores/" + dto.getId()))
                .body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AutorDto> actualizar(@PathVariable Integer id,
                                               @Valid @RequestBody ActualizarAutorCommand body) {
        var dto = actualizarHandler.handle(
                ActualizarAutorCommand.builder()
                        .idAutor(id) // el path domina al body
                        .nombre(body.getNombre())
                        .fechaNacimiento(body.getFechaNacimiento())
                        .nacionalidad(body.getNacionalidad())
                        .build()
        );
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        eliminarHandler.handle(EliminarAutorCommand.builder().idAutor(id).build());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutorDto> porId(@PathVariable Integer id) {
        var dto = obtenerHandler.handle(ObtenerAutorQuery.builder().idAutor(id).build());
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<AutorDto>> todos() {
        var lista = listarHandler.handle(ListarAutoresQuery.builder().build());
        return ResponseEntity.ok(lista);
    }
}
