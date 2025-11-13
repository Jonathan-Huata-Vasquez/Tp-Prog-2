package biblioteca.biblioteca.web.controller;

import biblioteca.biblioteca.application.command.ActualizarLectorCommand;
import biblioteca.biblioteca.application.command.ActualizarLectorCommandHandler;
import biblioteca.biblioteca.application.command.CrearLectorCommand;
import biblioteca.biblioteca.application.command.CrearLectorCommandHandler;
import biblioteca.biblioteca.application.query.*;
import biblioteca.biblioteca.web.dto.LectorDto;
import biblioteca.biblioteca.web.dto.PrestamoDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/lectores")
@RequiredArgsConstructor
public class LectoresController {

    private final CrearLectorCommandHandler crearHandler;
    private final ActualizarLectorCommandHandler actualizarHandler;
    private final ObtenerLectorQueryHandler obtenerHandler;
    private final ListarLectoresMorososQueryHandler morososHandler;

    // reutilizamos handler existente de Prestamos para endpoint de conveniencia
    private final PrestamosActivosDeQueryHandler prestamosActivosDeHandler;

    @PostMapping
    public ResponseEntity<LectorDto> crear(@Valid @RequestBody CrearLectorCommand body) {
        var dto = crearHandler.handle(body);
        return ResponseEntity
                .created(URI.create("/api/lectores/" + dto.getId()))
                .body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LectorDto> actualizar(@PathVariable Integer id,
                                                @Valid @RequestBody ActualizarLectorCommand body) {
        var dto = actualizarHandler.handle(
                ActualizarLectorCommand.builder()
                        .idLector(id) // aseguramos que el path domine sobre el body
                        .nuevoNombre(body.getNuevoNombre())
                        .build()
        );
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LectorDto> porId(@PathVariable Integer id) {
        var dto = obtenerHandler.handle(ObtenerLectorQuery.builder().idLector(id).build());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/morosos")
    public ResponseEntity<List<LectorDto>> morosos(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        var fecha = (hasta != null) ? hasta : LocalDate.now();
        var lista = morososHandler.handle(ListarLectoresMorososQuery.builder().hastaFecha(fecha).build());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}/prestamos-activos")
    public ResponseEntity<List<PrestamoDto>> prestamosActivos(@PathVariable Integer id) {
        var lista = prestamosActivosDeHandler.handle(PrestamosActivosDeQuery.builder().idLector(id).build());
        return ResponseEntity.ok(lista);
    }
}
