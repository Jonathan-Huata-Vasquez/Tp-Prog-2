package biblioteca.biblioteca.web.controller;

import biblioteca.biblioteca.application.command.DevolverCopiaCommand;
import biblioteca.biblioteca.application.command.DevolverCopiaCommandHandler;
import biblioteca.biblioteca.application.command.PrestarCopiaCommand;
import biblioteca.biblioteca.application.command.PrestarCopiaCommandHandler;
import biblioteca.biblioteca.application.query.ObtenerPrestamoQuery;
import biblioteca.biblioteca.application.query.ObtenerPrestamoQueryHandler;
import biblioteca.biblioteca.application.query.PrestamosActivosDeQuery;
import biblioteca.biblioteca.application.query.PrestamosActivosDeQueryHandler;
import biblioteca.biblioteca.web.dto.PrestamoDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
@RequiredArgsConstructor
public class PrestamosController {

    private final PrestarCopiaCommandHandler prestarHandler;
    private final DevolverCopiaCommandHandler devolverHandler;
    private final ObtenerPrestamoQueryHandler obtenerHandler;
    private final PrestamosActivosDeQueryHandler activosDeHandler;

    @PostMapping("/prestar")
    public ResponseEntity<PrestamoDto> prestar(@Valid @RequestBody PrestarCopiaCommand body) {
        var dto = prestarHandler.handle(body);
        return ResponseEntity
                .created(URI.create("/api/prestamos/" + dto.getId()))
                .body(dto);
    }

    @PostMapping("/devolver")
    public ResponseEntity<PrestamoDto> devolver(@Valid @RequestBody DevolverCopiaCommand body) {
        var dto = devolverHandler.handle(body);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestamoDto> porId(@PathVariable Integer id) {
        var dto = obtenerHandler.handle(ObtenerPrestamoQuery.builder().idPrestamo(id).build());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<PrestamoDto>> activosPorLector(@RequestParam Integer idLector) {
        var lista = activosDeHandler.handle(PrestamosActivosDeQuery.builder().idLector(idLector).build());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/demo")
    public ResponseEntity<PrestamoDto> demo() {
        PrestamoDto dto = PrestamoDto.builder()
                .id(123)
                .idLector(10)
                .idCopia(55)
                .fechaInicio(LocalDate.of(2025, 1, 10))
                .fechaVencimiento(LocalDate.of(2025, 1, 31)) // +21 días aprox.
                .fechaDevolucion(null) // aún abierto
                .build();

        return ResponseEntity.ok(dto);
    }


}
