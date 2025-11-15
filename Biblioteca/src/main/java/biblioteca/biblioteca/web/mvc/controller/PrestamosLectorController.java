package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PrestamosLectorController {

    // private final IPrestamosDeLectorQuery prestamosDeLectorQuery;

    @GetMapping("/lector/prestamos")
    public String prestamos(@AuthenticationPrincipal UsuarioDetalles usuario, Model model) {

        // Placeholder de resumen
        Map<String, Object> resumen = Map.of(
                "cantidadActivos", 2
        );

        // Placeholder de préstamos (reemplazar por query real con el lectorId del usuario)
        List<Map<String, Object>> prestamos = List.of(
                Map.of(
                        "libroId", 1,
                        "tituloLibro", "Cien años de soledad",
                        "autor", "Gabriel García Márquez",
                        "idEjemplar", 145,
                        "fechaPrestamo", "20/10/2025",
                        "fechaVencimiento", "10/11/2025",
                        "estado", "ACTIVO"
                ),
                Map.of(
                        "libroId", 2,
                        "tituloLibro", "Introducción a la algoritmia",
                        "autor", "J. Pérez · M. López",
                        "idEjemplar", 12,
                        "fechaPrestamo", "01/09/2025",
                        "fechaVencimiento", "22/09/2025",
                        "estado", "VENCIDO"
                ),
                Map.of(
                        "libroId", 3,
                        "tituloLibro", "El nombre del viento",
                        "autor", "Patrick Rothfuss",
                        "idEjemplar", 87,
                        "fechaPrestamo", "02/08/2025",
                        "fechaVencimiento", "23/08/2025",
                        "estado", "DEVUELTO"
                )
        );

        model.addAttribute("resumen", resumen);
        model.addAttribute("prestamos", prestamos);

        // Ejemplo real:
        // var resultado = prestamosDeLectorQuery.listar(usuario.getLectorId());
        // model.addAttribute("resumen", resultado.getResumen());
        // model.addAttribute("prestamos", resultado.getPrestamos());

        return "lector/prestamos";
    }
}
