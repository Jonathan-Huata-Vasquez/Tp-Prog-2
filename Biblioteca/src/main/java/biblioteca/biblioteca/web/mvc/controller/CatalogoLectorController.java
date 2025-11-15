package biblioteca.biblioteca.web.mvc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CatalogoLectorController {

    // private final ILibroCatalogoQuery libroCatalogoQuery;

    @GetMapping({"/lector/catalogo", "/catalogo", "/catalogo/buscar"})
    public String catalogo(
            @RequestParam(name = "q", required = false) String q,
            Model model
    ) {
        // Placeholder de datos para que la vista renderice sin NPE:
        model.addAttribute("criterioBusqueda", q);
        model.addAttribute("totalLibros", 0);
        model.addAttribute("libros", java.util.List.of());

        // Ejemplo cuando conectes el query real:
        // var resultado = libroCatalogoQuery.listar(q); // List<LibroCatalogoItemDto>
        // model.addAttribute("libros", resultado);
        // model.addAttribute("totalLibros", resultado.size());

        return "lector/catalogo";
    }
}
