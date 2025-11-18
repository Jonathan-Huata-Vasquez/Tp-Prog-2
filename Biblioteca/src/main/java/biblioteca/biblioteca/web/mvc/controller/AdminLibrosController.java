package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.application.query.ListarCatalogoQuery;
import biblioteca.biblioteca.application.query.ListarCatalogoQueryHandler;
import biblioteca.biblioteca.application.command.CrearLibroCommand;
import biblioteca.biblioteca.application.command.CrearLibroCommandHandler;
import biblioteca.biblioteca.application.query.ListarAutoresQueryHandler;
import biblioteca.biblioteca.application.query.ListarEditorialesQueryHandler;
import biblioteca.biblioteca.domain.model.Categoria;
import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import biblioteca.biblioteca.web.helper.ControllerHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AdminLibrosController {

    private final ControllerHelper controllerHelper;
    private final ListarCatalogoQueryHandler listarCatalogoCommandHandler;
    private final ListarAutoresQueryHandler listarAutoresQueryHandler;
    private final ListarEditorialesQueryHandler listarEditorialesQueryHandler;

    private final CrearLibroCommandHandler crearLibroCommandHandler;

    @GetMapping("/admin/libros")
    public String listar(@AuthenticationPrincipal UsuarioDetalles usuario,
                         HttpSession session,
                         Model model) {
        var result = listarCatalogoCommandHandler.handle(new ListarCatalogoQuery(null));
        model.addAttribute("libros", result.getItems()); // lista de LibroCatalogoItemDto

        model.addAttribute("admin", usuario);
        model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);

        return "admin/admin-libros";
    }


    @GetMapping("/admin/libros/nuevo")
    public String nuevo(@AuthenticationPrincipal UsuarioDetalles usuario,
                        HttpSession session,
                        Model model) {
        model.addAttribute("admin", usuario);
        model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);
        // DTO vacío para el formulario
        model.addAttribute("libroForm", biblioteca.biblioteca.application.command.CrearLibroCommand.builder().build());

        // Cargar autores
        var autores = listarAutoresQueryHandler.handle(biblioteca.biblioteca.application.query.ListarAutoresQuery.builder().build());
        model.addAttribute("autores", autores);

        // Cargar editoriales
        var editoriales = listarEditorialesQueryHandler.handle(biblioteca.biblioteca.application.query.ListarEditorialesQuery.builder().build());
        model.addAttribute("editoriales", editoriales);

        // Cargar categorías
        model.addAttribute("categorias", Arrays.asList(Categoria.values()));

        return "admin/admin-libro-form";
    }

    @PostMapping("/admin/libros")
    public String crear(@AuthenticationPrincipal UsuarioDetalles usuario,
                        HttpSession session,
                        Model model,
                        @Validated @ModelAttribute("libroForm") CrearLibroCommand libroForm,
                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            prepararModeloForm(model, usuario, session);
            model.addAttribute("autores", listarAutoresQueryHandler.handle(biblioteca.biblioteca.application.query.ListarAutoresQuery.builder().build()));
            model.addAttribute("editoriales", listarEditorialesQueryHandler.handle(biblioteca.biblioteca.application.query.ListarEditorialesQuery.builder().build()));
            model.addAttribute("categorias", Arrays.asList(Categoria.values()));
            return "admin/admin-libro-form";
        }
        try {
            crearLibroCommandHandler.handle(libroForm);
        } catch (Exception e) {
            prepararModeloForm(model, usuario, session);
            model.addAttribute("autores", listarAutoresQueryHandler.handle(biblioteca.biblioteca.application.query.ListarAutoresQuery.builder().build()));
            model.addAttribute("editoriales", listarEditorialesQueryHandler.handle(biblioteca.biblioteca.application.query.ListarEditorialesQuery.builder().build()));
            model.addAttribute("categorias", Arrays.asList(Categoria.values()));
            model.addAttribute("errorCrear", e.getMessage());
            return "admin/admin-libro-form";
        }
        return "redirect:/admin/libros";
    }

    private void prepararModeloForm(Model model, UsuarioDetalles usuario, HttpSession session) {
        model.addAttribute("admin", usuario);
        model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);
    }
    // Iniciales calculadas ahora vía ControllerHelper.calcularIniciales(usuario)
}
