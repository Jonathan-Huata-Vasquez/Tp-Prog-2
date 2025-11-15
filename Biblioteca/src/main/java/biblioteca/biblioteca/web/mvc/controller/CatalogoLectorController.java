package biblioteca.biblioteca.web.mvc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CatalogoLectorController {

    // private final ILibroCatalogoQuery libroCatalogoQuery;

    @GetMapping({"/lector/catalogo", "/catalogo", "/catalogo/buscar"})
    public String catalogo(
            @RequestParam(name = "q", required = false) String q,
            Model model
    ) {
        model.addAttribute("criterioBusqueda", q);

        // Placeholders (reemplazar por resultados reales)
        List<Map<String, Object>> libros = List.of(
                Map.of(
                        "id", 1,
                        "titulo", "Cien años de soledad",
                        "autor", "Gabriel García Márquez",
                        "categoria", "Novela",
                        "anio", 1967,
                        "descripcionCorta", "Obra clave del realismo mágico.",
                        "copiasDisponibles", 2,
                        "totalCopias", 4
                ),
                Map.of(
                        "id", 2,
                        "titulo", "Introducción a la algoritmia",
                        "autor", "J. Pérez · M. López",
                        "categoria", "Informática",
                        "anio", 2015,
                        "descripcionCorta", "Manual introductorio.",
                        "copiasDisponibles", 0,
                        "totalCopias", 1
                )
        );

        model.addAttribute("libros", libros);
        model.addAttribute("totalLibros", libros.size());

        // Ejemplo real:
        // var resultado = libroCatalogoQuery.listar(q);
        // model.addAttribute("libros", resultado);
        // model.addAttribute("totalLibros", resultado.size());

        return "lector/catalogo";
    }
}
