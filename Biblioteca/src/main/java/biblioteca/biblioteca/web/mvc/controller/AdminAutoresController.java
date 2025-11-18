package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.application.query.ListarAutoresQuery;
import biblioteca.biblioteca.application.query.ListarAutoresQueryHandler;
import biblioteca.biblioteca.application.command.EliminarAutorCommand;
import biblioteca.biblioteca.application.command.EliminarAutorCommandHandler;
import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import biblioteca.biblioteca.web.dto.AutorDto;
import biblioteca.biblioteca.web.dto.AutorFormDto;
import biblioteca.biblioteca.application.command.CrearAutorCommandHandler;
import biblioteca.biblioteca.application.command.CrearAutorCommand;
import biblioteca.biblioteca.application.command.ActualizarAutorCommandHandler;
import biblioteca.biblioteca.application.command.ActualizarAutorCommand;
import biblioteca.biblioteca.application.query.ObtenerAutorQueryHandler;
import biblioteca.biblioteca.application.query.ObtenerAutorQuery;
import biblioteca.biblioteca.web.helper.ControllerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class AdminAutoresController {
    private final ControllerHelper controllerHelper;
    private final ListarAutoresQueryHandler listarAutoresQueryHandler;
    private final EliminarAutorCommandHandler eliminarAutorCommandHandler;
    private final CrearAutorCommandHandler crearAutorCommandHandler;
    private final ActualizarAutorCommandHandler actualizarAutorCommandHandler;
    private final ObtenerAutorQueryHandler obtenerAutorQueryHandler;

    @GetMapping("/admin/autores")
    public String listar(@AuthenticationPrincipal UsuarioDetalles usuario, HttpSession session, Model model) {
        var autores = listarAutoresQueryHandler.handle(new ListarAutoresQuery());
        model.addAttribute("autores", autores);
        model.addAttribute("admin", usuario);
        model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);
        return "admin/admin-autores";
    }

    @PostMapping("/admin/autores/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, @AuthenticationPrincipal UsuarioDetalles usuario, HttpSession session, Model model) {
        try {
            var cmd = EliminarAutorCommand.builder().idAutor(id).build();
            eliminarAutorCommandHandler.handle(cmd);
            return "redirect:/admin/autores";
        } catch (Exception e) {
            var autores = listarAutoresQueryHandler.handle(new ListarAutoresQuery());
            model.addAttribute("autores", autores);
            model.addAttribute("errorEliminar", e.getMessage());
            model.addAttribute("admin", usuario);
            model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
            controllerHelper.agregarRolActualAlModelo(model, usuario, session);
            return "admin/admin-autores";
        }
    }

    @GetMapping("/admin/autores/crear")
    public String mostrarCrearForm(@AuthenticationPrincipal UsuarioDetalles usuario, HttpSession session, Model model) {
        model.addAttribute("autorFormDto", new AutorFormDto());
        model.addAttribute("formMode", "crear");
        model.addAttribute("formAction", "/admin/autores/crear");
        model.addAttribute("admin", usuario);
        model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);
        return "admin/admin-autor-form";
    }

    @PostMapping("/admin/autores/crear")
    public String crearAutor(AutorFormDto autorFormDto, @AuthenticationPrincipal UsuarioDetalles usuario, HttpSession session, Model model) {
        try {
            var cmd = CrearAutorCommand.builder()
                    .nombre(autorFormDto.getNombre())
                    .fechaNacimiento(autorFormDto.getFechaNacimiento())
                    .nacionalidad(autorFormDto.getNacionalidad())
                    .build();
            crearAutorCommandHandler.handle(cmd);
            return "redirect:/admin/autores";
        } catch (Exception e) {
            model.addAttribute("autorFormDto", autorFormDto);
            model.addAttribute("formMode", "crear");
            model.addAttribute("formAction", "/admin/autores/crear");
            model.addAttribute("errorForm", e.getMessage());
            model.addAttribute("admin", usuario);
            model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
            controllerHelper.agregarRolActualAlModelo(model, usuario, session);
            return "admin/admin-autor-form";
        }
    }

    @GetMapping("/admin/autores/{id}/editar")
    public String mostrarEditarForm(@PathVariable Integer id, @AuthenticationPrincipal UsuarioDetalles usuario, HttpSession session, Model model) {
        try {
            var autorDto = obtenerAutorQueryHandler.handle(ObtenerAutorQuery.builder().idAutor(id).build());
            var formDto = AutorFormDto.builder()
                    .id(autorDto.getId())
                    .nombre(autorDto.getNombre())
                    .fechaNacimiento(autorDto.getFechaNacimiento())
                    .nacionalidad(autorDto.getNacionalidad())
                    .build();
            model.addAttribute("autorFormDto", formDto);
            model.addAttribute("formMode", "editar");
            model.addAttribute("formAction", "/admin/autores/" + id + "/editar");
            model.addAttribute("admin", usuario);
            model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
            controllerHelper.agregarRolActualAlModelo(model, usuario, session);
            return "admin/admin-autor-form";
        } catch (Exception e) {
            return "redirect:/admin/autores?errorEditar=" + e.getMessage();
        }
    }

    @PostMapping("/admin/autores/{id}/editar")
    public String editarAutor(@PathVariable Integer id, AutorFormDto autorFormDto, @AuthenticationPrincipal UsuarioDetalles usuario, HttpSession session, Model model) {
        try {
            var cmd = ActualizarAutorCommand.builder()
                    .idAutor(id)
                    .nombre(autorFormDto.getNombre())
                    .fechaNacimiento(autorFormDto.getFechaNacimiento())
                    .nacionalidad(autorFormDto.getNacionalidad())
                    .build();
            actualizarAutorCommandHandler.handle(cmd);
            return "redirect:/admin/autores";
        } catch (Exception e) {
            autorFormDto.setId(id);
            model.addAttribute("autorFormDto", autorFormDto);
            model.addAttribute("formMode", "editar");
            model.addAttribute("formAction", "/admin/autores/" + id + "/editar");
            model.addAttribute("errorForm", e.getMessage());
            model.addAttribute("admin", usuario);
            model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
            controllerHelper.agregarRolActualAlModelo(model, usuario, session);
            return "admin/admin-autor-form";
        }
    }
}
