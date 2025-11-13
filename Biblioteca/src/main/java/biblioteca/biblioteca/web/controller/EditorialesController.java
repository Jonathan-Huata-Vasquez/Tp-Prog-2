package biblioteca.biblioteca.web.controller;

import biblioteca.biblioteca.application.command.*;
import biblioteca.biblioteca.application.query.ListarEditorialesQuery;
import biblioteca.biblioteca.application.query.ListarEditorialesQueryHandler;
import biblioteca.biblioteca.application.query.ObtenerEditorialQuery;
import biblioteca.biblioteca.application.query.ObtenerEditorialQueryHandler;
import biblioteca.biblioteca.web.dto.EditorialDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/editoriales")
@RequiredArgsConstructor
public class EditorialesController {

    private final CrearEditorialCommandHandler crearHandler;
    private final ActualizarEditorialCommandHandler actualizarHandler;
    private final EliminarEditorialCommandHandler eliminarHandler;
    private final ObtenerEditorialQueryHandler obtenerHandler;
    private final ListarEditorialesQueryHandler listarHandler;

    @PostMapping
    public ResponseEntity<EditorialDto> crear(@Valid @RequestBody CrearEditorialCommand body) {
        var dto = crearHandler.handle(body);
        return ResponseEntity
                .created(URI.create("/api/editoriales/" + dto.getId()))
                .body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EditorialDto> actualizar(@PathVariable Integer id,
                                                   @Valid @RequestBody ActualizarEditorialCommand body) {
        var dto = actualizarHandler.handle(
                ActualizarEditorialCommand.builder()
                        .idEditorial(id) // el path domina
                        .nombre(body.getNombre())
                        .build()
        );
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        eliminarHandler.handle(EliminarEditorialCommand.builder().idEditorial(id).build());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EditorialDto> porId(@PathVariable Integer id) {
        var dto = obtenerHandler.handle(ObtenerEditorialQuery.builder().idEditorial(id).build());
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<EditorialDto>> todas() {
        var lista = listarHandler.handle(ListarEditorialesQuery.builder().build());
        return ResponseEntity.ok(lista);
    }
}
