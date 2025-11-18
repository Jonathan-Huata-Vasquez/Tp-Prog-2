   

package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.application.command.*;
import biblioteca.biblioteca.application.query.ListarCatalogoQuery;
import biblioteca.biblioteca.application.query.ListarCatalogoQueryHandler;
import biblioteca.biblioteca.web.dto.LibroFormDto;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;



@Controller
@RequiredArgsConstructor
@Slf4j
public class AdminLibrosController {

    private final ControllerHelper controllerHelper;
    private final ListarCatalogoQueryHandler listarCatalogoQueryHandler;
    private final ListarAutoresQueryHandler listarAutoresQueryHandler;
    private final ListarEditorialesQueryHandler listarEditorialesQueryHandler;
    private final CrearLibroCommandHandler crearLibroCommandHandler;
    private final ActualizarLibroCommandHandler actualizarLibroCommandHandler;
    private final EliminarLibroCommandHandler eliminarLibroCommandHandler;

    @GetMapping("/admin/libros")
    public String listar(@AuthenticationPrincipal UsuarioDetalles usuario,
                         HttpSession session,
                         Model model) {
        var result = listarCatalogoQueryHandler.handle(new ListarCatalogoQuery(null));
        model.addAttribute("libros", result.getItems());
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
        model.addAttribute("libroForm", new LibroFormDto());
        model.addAttribute("autores", listarAutoresQueryHandler.handle(biblioteca.biblioteca.application.query.ListarAutoresQuery.builder().build()));
        model.addAttribute("editoriales", listarEditorialesQueryHandler.handle(biblioteca.biblioteca.application.query.ListarEditorialesQuery.builder().build()));
        model.addAttribute("categorias", Arrays.asList(Categoria.values()));
        model.addAttribute("formMode", "crear");
        return "admin/admin-libro-form";
    }

    @PostMapping("/admin/libros")
    public String crear(@AuthenticationPrincipal UsuarioDetalles usuario,
                        HttpSession session,
                        Model model,
                        @Validated @ModelAttribute("libroForm") LibroFormDto libroForm,
                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            prepararModeloForm(model, usuario, session);
            model.addAttribute("autores", listarAutoresQueryHandler.handle(biblioteca.biblioteca.application.query.ListarAutoresQuery.builder().build()));
            model.addAttribute("editoriales", listarEditorialesQueryHandler.handle(biblioteca.biblioteca.application.query.ListarEditorialesQuery.builder().build()));
            model.addAttribute("categorias", Arrays.asList(Categoria.values()));
            model.addAttribute("formMode", "crear");
            return "admin/admin-libro-form";
        }
        try {
            var cmd = CrearLibroCommand.builder()
                .titulo(libroForm.getTitulo())
                .idAutor(libroForm.getIdAutor())
                .idEditorial(libroForm.getIdEditorial())
                .categoria(Categoria.valueOf(libroForm.getCategoria()))
                .anioPublicacion(libroForm.getAnioPublicacion())
                .descripcion(libroForm.getDescripcion())
                .build();
            crearLibroCommandHandler.handle(cmd);
            var result = listarCatalogoQueryHandler.handle(new ListarCatalogoQuery(null));
            model.addAttribute("libros", result.getItems());
            model.addAttribute("mensajeExito", "Libro creado exitosamente");
            model.addAttribute("admin", usuario);
            model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
            controllerHelper.agregarRolActualAlModelo(model, usuario, session);
            return "admin/admin-libros";
        } catch (Exception e) {
            prepararModeloForm(model, usuario, session);
            model.addAttribute("autores", listarAutoresQueryHandler.handle(biblioteca.biblioteca.application.query.ListarAutoresQuery.builder().build()));
            model.addAttribute("editoriales", listarEditorialesQueryHandler.handle(biblioteca.biblioteca.application.query.ListarEditorialesQuery.builder().build()));
            model.addAttribute("categorias", Arrays.asList(Categoria.values()));
            model.addAttribute("formMode", "crear");
            model.addAttribute("errorCrear", e.getMessage());
            return "admin/admin-libro-form";
        }
    }

    @GetMapping("/admin/libros/{id}/editar")
    public String editar(@PathVariable Integer id,
                        @AuthenticationPrincipal UsuarioDetalles usuario,
                        HttpSession session,
                        Model model) {
        var libroDto = listarCatalogoQueryHandler.handle(new ListarCatalogoQuery(null)).getItems()
            .stream().filter(l -> l.getId().equals(id)).findFirst().orElse(null);
        if (libroDto == null) {
            model.addAttribute("errorEditar", "Libro inexistente: " + id);
            var result = listarCatalogoQueryHandler.handle(new ListarCatalogoQuery(null));
            model.addAttribute("libros", result.getItems());
            model.addAttribute("admin", usuario);
            model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
            controllerHelper.agregarRolActualAlModelo(model, usuario, session);
            return "admin/admin-libros";
        }
        model.addAttribute("admin", usuario);
        model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);
        // Mapear LibroCatalogoItemDto a LibroFormDto
        var libroForm = new LibroFormDto();
        libroForm.setId(libroDto.getId());
        libroForm.setTitulo(libroDto.getTitulo());
        libroForm.setIdAutor(libroDto.getIdAutor());
        libroForm.setIdEditorial(libroDto.getIdEditorial());
        libroForm.setCategoria(libroDto.getCategoria());
        libroForm.setAnioPublicacion(libroDto.getAnioPublicacion());
        libroForm.setDescripcion(libroDto.getDescripcion());
        model.addAttribute("libroForm", libroForm);
        model.addAttribute("autores", listarAutoresQueryHandler.handle(biblioteca.biblioteca.application.query.ListarAutoresQuery.builder().build()));
        model.addAttribute("editoriales", listarEditorialesQueryHandler.handle(biblioteca.biblioteca.application.query.ListarEditorialesQuery.builder().build()));
        model.addAttribute("categorias", Arrays.asList(Categoria.values()));
        model.addAttribute("formMode", "editar");
        return "admin/admin-libro-form";
    }

    @PostMapping("/admin/libros/{id}/editar")
    public String actualizar(@PathVariable Integer id,
                            @AuthenticationPrincipal UsuarioDetalles usuario,
                            HttpSession session,
                            Model model,
                            @Validated @ModelAttribute("libroForm") LibroFormDto libroForm,
                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            prepararModeloForm(model, usuario, session);
            model.addAttribute("autores", listarAutoresQueryHandler.handle(biblioteca.biblioteca.application.query.ListarAutoresQuery.builder().build()));
            model.addAttribute("editoriales", listarEditorialesQueryHandler.handle(biblioteca.biblioteca.application.query.ListarEditorialesQuery.builder().build()));
            model.addAttribute("categorias", Arrays.asList(Categoria.values()));
            model.addAttribute("formMode", "editar");
            return "admin/admin-libro-form";
        }
        try {
            var cmd = ActualizarLibroCommand.builder()
                .idLibro(id)
                .titulo(libroForm.getTitulo())
                .idAutor(libroForm.getIdAutor())
                .idEditorial(libroForm.getIdEditorial())
                .categoria(Categoria.valueOf(libroForm.getCategoria()))
                .anioPublicacion(libroForm.getAnioPublicacion())
                .descripcion(libroForm.getDescripcion())
                .build();
            actualizarLibroCommandHandler.handle(cmd);
            var result = listarCatalogoQueryHandler.handle(new ListarCatalogoQuery(null));
            model.addAttribute("libros", result.getItems());
            model.addAttribute("mensajeExito", "Libro actualizado exitosamente");
            model.addAttribute("admin", usuario);
            model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
            controllerHelper.agregarRolActualAlModelo(model, usuario, session);
            return "admin/admin-libros";
        } catch (Exception e) {
            prepararModeloForm(model, usuario, session);
            model.addAttribute("autores", listarAutoresQueryHandler.handle(biblioteca.biblioteca.application.query.ListarAutoresQuery.builder().build()));
            model.addAttribute("editoriales", listarEditorialesQueryHandler.handle(biblioteca.biblioteca.application.query.ListarEditorialesQuery.builder().build()));
            model.addAttribute("categorias", Arrays.asList(Categoria.values()));
            model.addAttribute("formMode", "editar");
            model.addAttribute("errorEditar", e.getMessage());
            return "admin/admin-libro-form";
        }
    }

    @PostMapping("/admin/libros/{id}/eliminar")
    public String eliminar(@PathVariable Integer id,
                          @AuthenticationPrincipal UsuarioDetalles usuario,
                          HttpSession session,
                          Model model) {
        try {
            eliminarLibroCommandHandler.handle(EliminarLibroCommand.builder().idLibro(id).build());
            return "redirect:/admin/libros";
        } catch (Exception e) {
            var result = listarCatalogoQueryHandler.handle(new ListarCatalogoQuery(null));
            model.addAttribute("libros", result.getItems());
            model.addAttribute("errorEliminar", e.getMessage());
            model.addAttribute("admin", usuario);
            model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
            controllerHelper.agregarRolActualAlModelo(model, usuario, session);
            return "admin/admin-libros";
        }
    }

    private void prepararModeloForm(Model model, UsuarioDetalles usuario, HttpSession session) {
        model.addAttribute("admin", usuario);
        model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);
    }
}
