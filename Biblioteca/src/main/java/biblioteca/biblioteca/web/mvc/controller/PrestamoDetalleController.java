package biblioteca.biblioteca.web.mvc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PrestamoDetalleController {

    // private final IDetallePrestamoQuery detallePrestamoQuery;
    // private final IRolActualProvider rolActualProvider;

    @GetMapping("/prestamos/{id}")
    public String detalle(@PathVariable("id") Integer id, Model model) {

        // Placeholder (reemplazar por query real)
        Map<String, Object> prestamo = new HashMap<>();
        prestamo.put("idPrestamo", id);
        prestamo.put("codigo", String.format("P-%05d", id));
        prestamo.put("estado", "ACTIVO");                  // ACTIVO | VENCIDO | DEVUELTO
        prestamo.put("fechaPrestamo", "20/10/2025");
        prestamo.put("fechaVencimiento", "10/11/2025");
        prestamo.put("fechaDevolucion", null);             // null permitido con HashMap
        prestamo.put("diasAtraso", 0);

        prestamo.put("idLector", 102);
        prestamo.put("nombreLector", "Juan Pérez");

        prestamo.put("tituloLibro", "Cien años de soledad");
        prestamo.put("autor", "Gabriel García Márquez");
        prestamo.put("editorial", "Editorial Sudamericana");
        prestamo.put("anioPublicacion", 1967);
        prestamo.put("urlPortada", null);                  // null permitido

        model.addAttribute("prestamo", prestamo);
        model.addAttribute("rolActual", "LECTOR"); // o "BIBLIOTECARIO" según el usuario autenticado

        // Ejemplo real:
        // var detalle = detallePrestamoQuery.obtener(id, usuarioActual);
        // model.addAttribute("prestamo", detalle);
        // model.addAttribute("rolActual", rolActualProvider.rolDeSesion());

        return "prestamos/detalle-prestamo";
    }
}
