package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.application.command.ListarCatalogoCommand;
import biblioteca.biblioteca.application.command.ListarCatalogoCommandHandler;
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

    private final ListarCatalogoCommandHandler listarHandler;

    @GetMapping({"/catalogo", "/lector/catalogo", "/bibliotecario/catalogo"})
    public String catalogo(@RequestParam(value = "q", required = false) String q, Model model) {
        var result = listarHandler.handle(new ListarCatalogoCommand(q));
        model.addAttribute("totalLibros", result.getTotalLibros());
        model.addAttribute("criterioBusqueda", result.getCriterio());
        model.addAttribute("libros", result.getItems()); // lista de LibroCatalogoItemDto
        return "lector/catalogo";
    }
}
