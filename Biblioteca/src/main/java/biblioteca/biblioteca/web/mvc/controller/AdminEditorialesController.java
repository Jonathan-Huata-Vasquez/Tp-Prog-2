package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.application.command.ActualizarEditorialCommandHandler;
import biblioteca.biblioteca.application.command.CrearEditorialCommandHandler;
import biblioteca.biblioteca.application.query.ListarEditorialesQuery;
import biblioteca.biblioteca.application.query.ListarEditorialesQueryHandler;
import biblioteca.biblioteca.application.command.EliminarEditorialCommand;
import biblioteca.biblioteca.application.command.EliminarEditorialCommandHandler;
import biblioteca.biblioteca.application.query.ObtenerEditorialQueryHandler;
import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import biblioteca.biblioteca.web.dto.EditorialDto;
import biblioteca.biblioteca.web.helper.ControllerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class AdminEditorialesController {
    private final ControllerHelper controllerHelper;
    private final ListarEditorialesQueryHandler listarEditorialesQueryHandler;
    private final EliminarEditorialCommandHandler eliminarEditorialCommandHandler;
    private final CrearEditorialCommandHandler crearEditorialCommandHandler;
    private final ActualizarEditorialCommandHandler actualizarEditorialCommandHandler;
    private final ObtenerEditorialQueryHandler obtenerEditorialQueryHandler;
    @GetMapping("/admin/editoriales/nueva")
    public String nueva(@AuthenticationPrincipal UsuarioDetalles usuario, HttpSession session, Model model) {
        model.addAttribute("admin", usuario);
        model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);
        model.addAttribute("editorialForm", new biblioteca.biblioteca.web.dto.EditorialFormDto());
        model.addAttribute("formMode", "crear");
        model.addAttribute("formAction", "/admin/editoriales/nueva");
        return "admin/admin-editorial-form";
    }

    @PostMapping("/admin/editoriales/nueva")
    public String crear(@AuthenticationPrincipal UsuarioDetalles usuario, HttpSession session, Model model,
                       @ModelAttribute("editorialForm") biblioteca.biblioteca.web.dto.EditorialFormDto editorialForm) {
        try {
            var cmd = biblioteca.biblioteca.application.command.CrearEditorialCommand.builder()
                .nombre(editorialForm.getNombre())
                .build();
            crearEditorialCommandHandler.handle(cmd);
            return "redirect:/admin/editoriales";
        } catch (Exception e) {
            model.addAttribute("errorCrear", e.getMessage());
            model.addAttribute("editorialForm", editorialForm);
            model.addAttribute("formMode", "crear");
            model.addAttribute("formAction", "/admin/editoriales/nueva");
            model.addAttribute("admin", usuario);
            model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
            controllerHelper.agregarRolActualAlModelo(model, usuario, session);
            return "admin/admin-editorial-form";
        }
    }

    @GetMapping("/admin/editoriales/{id}/editar")
    public String editar(@PathVariable Integer id, @AuthenticationPrincipal UsuarioDetalles usuario, HttpSession session, Model model) {
        var dto = obtenerEditorialQueryHandler.handle(biblioteca.biblioteca.application.query.ObtenerEditorialQuery.builder().idEditorial(id).build());
        var form = new biblioteca.biblioteca.web.dto.EditorialFormDto(dto.getId(), dto.getNombre());
        model.addAttribute("editorialForm", form);
        model.addAttribute("formMode", "editar");
        model.addAttribute("formAction", "/admin/editoriales/" + id + "/editar");
        model.addAttribute("admin", usuario);
        model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);
        return "admin/admin-editorial-form";
    }

    @PostMapping("/admin/editoriales/{id}/editar")
    public String actualizar(@PathVariable Integer id, @AuthenticationPrincipal UsuarioDetalles usuario, HttpSession session, Model model,
                            @ModelAttribute("editorialForm") biblioteca.biblioteca.web.dto.EditorialFormDto editorialForm) {
        try {
            var cmd = biblioteca.biblioteca.application.command.ActualizarEditorialCommand.builder()
                .idEditorial(id)
                .nombre(editorialForm.getNombre())
                .build();
            actualizarEditorialCommandHandler.handle(cmd);
            return "redirect:/admin/editoriales";
        } catch (Exception e) {
            model.addAttribute("errorCrear", e.getMessage());
            model.addAttribute("editorialForm", editorialForm);
            model.addAttribute("formMode", "editar");
            model.addAttribute("formAction", "/admin/editoriales/" + id + "/editar");
            model.addAttribute("admin", usuario);
            model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
            controllerHelper.agregarRolActualAlModelo(model, usuario, session);
            return "admin/admin-editorial-form";
        }
    }

    @GetMapping("/admin/editoriales")
    public String listar(@AuthenticationPrincipal UsuarioDetalles usuario, HttpSession session, Model model) {
        var editoriales = listarEditorialesQueryHandler.handle(new ListarEditorialesQuery());
        model.addAttribute("editoriales", editoriales);
        model.addAttribute("admin", usuario);
        model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);
        return "admin/admin-editoriales";
    }

    @PostMapping("/admin/editoriales/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, @AuthenticationPrincipal UsuarioDetalles usuario, HttpSession session, Model model) {
        try {
            var cmd = EliminarEditorialCommand.builder().idEditorial(id).build();
            eliminarEditorialCommandHandler.handle(cmd);
            return "redirect:/admin/editoriales";
        } catch (Exception e) {
            var editoriales = listarEditorialesQueryHandler.handle(new ListarEditorialesQuery());
            model.addAttribute("editoriales", editoriales);
            model.addAttribute("errorEliminar", e.getMessage());
            model.addAttribute("admin", usuario);
            model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
            controllerHelper.agregarRolActualAlModelo(model, usuario, session);
            return "admin/admin-editoriales";
        }
    }
}
