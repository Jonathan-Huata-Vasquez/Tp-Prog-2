package biblioteca.biblioteca.web.mvc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class LibroDetalleController {

    // private final IDetalleLibroQuery detalleLibroQuery;

    @GetMapping("/catalogo/libro/{id}")
    public String detalleLibro(@PathVariable("id") Integer id, Model model) {

        // Placeholder de “libro” (reemplazar por query real)
        Map<String, Object> libro = new HashMap<>();
        libro.put("id", id);
        libro.put("titulo", "Cien años de soledad");
        libro.put("autor", "Gabriel García Márquez");
        libro.put("categoria", "Novela");
        libro.put("anio", 1967);
        libro.put("editorial", "Editorial Sudamericana");
        libro.put("descripcion", "Historia de la familia Buendía en Macondo.");

        List<Map<String, Object>> copias = new ArrayList<>();
        copias.add(Map.of("id", 145, "disponible", false));
        copias.add(Map.of("id", 146, "disponible", true));
        copias.add(Map.of("id", 147, "disponible", false));
        copias.add(Map.of("id", 148, "disponible", true));

        libro.put("copias", copias);
        libro.put("totalCopias", copias.size());
        libro.put("copiasDisponibles", copias.stream().filter(c -> (boolean) c.get("disponible")).count());
        libro.put("prestadoAlLector", false); // calcular según el usuario autenticado

        model.addAttribute("libro", libro);

        // Ejemplo real:
        // var detalle = detalleLibroQuery.obtenerPorId(id, usuarioActual);
        // model.addAttribute("libro", detalle);

        return "lector/detalle-libro";
    }
}
