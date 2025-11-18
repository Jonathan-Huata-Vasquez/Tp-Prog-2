package biblioteca.biblioteca.web.mvc.controller;


import biblioteca.biblioteca.application.command.ActualizarCopiaCommand;
import biblioteca.biblioteca.application.command.ActualizarCopiaCommandHandler;
import biblioteca.biblioteca.application.command.CrearCopiaCommandHandler;
import biblioteca.biblioteca.application.query.*;
import biblioteca.biblioteca.domain.model.EstadoCopia;
import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import biblioteca.biblioteca.web.dto.CopiaFormDto;
import biblioteca.biblioteca.web.helper.ControllerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.lang.reflect.Array;
import java.util.ArrayList;

@Controller
@RequiredArgsConstructor
public class AdminCopiasController {

    private final ControllerHelper controllerHelper;
    private final ListarCopiasQueryHandler listarCopiasQueryHandler;
    private final CrearCopiaCommandHandler crearCopiaCommandHandler;
    private final ActualizarCopiaCommandHandler actualizarCopiaCommandHandler;
    private final ListarLibrosQueryHandler listarLibrosQueryHandler;
    private final ObtenerCopiaQueryHandler obtenerCopiaQueryHandler;

    @GetMapping("/admin/copias")
    public String listar(@AuthenticationPrincipal UsuarioDetalles usuario,
                         HttpSession session,
                         Model model) {
        var result = listarCopiasQueryHandler.handle(new ListarCopiasQuery());
        model.addAttribute("copias", result);
        model.addAttribute("admin", usuario);
        model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);
        return "admin/admin-copias";
    }


    @GetMapping("/admin/copias/nueva")
    public String nueva(@AuthenticationPrincipal UsuarioDetalles usuario,
                        HttpSession session,
                        Model model) {
        var libros = listarLibrosQueryHandler.handle(new ListarLibrosQuery());
        model.addAttribute("admin", usuario);
        model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);
        model.addAttribute("libros", libros);
        //model.addAttribute("estados", new EstadoCopia[]{EstadoCopia.EnBiblioteca, EstadoCopia.EnReparacion});
        model.addAttribute("copiaForm", new CopiaFormDto(null, null, EstadoCopia.EnBiblioteca));
        model.addAttribute("formMode", "crear");
        return "admin/admin-copia-form-crear";
    }

    @PostMapping("/admin/copias")
    public String crear(@AuthenticationPrincipal UsuarioDetalles usuario,
                        HttpSession session,
                        Model model,
                        @ModelAttribute("copiaForm") CopiaFormDto copiaForm) {

        try {
            var cmd = biblioteca.biblioteca.application.command.CrearCopiaCommand.builder()
                .idLibro(copiaForm.getIdLibro())
                .build();
            crearCopiaCommandHandler.handle(cmd);
            return "redirect:/admin/copias";
        } catch (Exception e) {
            var libros = listarLibrosQueryHandler.handle(new ListarLibrosQuery());
            model.addAttribute("errorCrear", e.getMessage());
            model.addAttribute("libros", libros);
            model.addAttribute("formMode", "crear");
            return "admin/admin-copia-form-crear";
        }
    }

    @PostMapping("/admin/copias/{id}/editar")
    public String editarPost(@PathVariable Integer id,
                            @AuthenticationPrincipal UsuarioDetalles usuario,
                            HttpSession session,
                            Model model,
                            @ModelAttribute("copiaForm") biblioteca.biblioteca.web.dto.CopiaFormDto copiaForm) {
        // ActualizarCopiaCommandHandler debe estar inyectado
        try {
            var cmd = ActualizarCopiaCommand.builder()
                .idCopia(id)
                .nuevoEstado(copiaForm.getEstado())
                .build();
            actualizarCopiaCommandHandler.handle(cmd);
            return "redirect:/admin/copias";
        } catch (Exception e) {
            var libros = listarLibrosQueryHandler.handle(new ListarLibrosQuery());
            model.addAttribute("errorEditar", e.getMessage());
            model.addAttribute("libros", libros);
            model.addAttribute("formMode", "editar");
            return "admin/admin-copia-form-editar";
        }
    }

    @GetMapping("/admin/copias/{id}/editar")
    public String editar(@PathVariable Integer id,
                        @AuthenticationPrincipal UsuarioDetalles usuario,
                        HttpSession session,
                        Model model) {
        var copia = obtenerCopiaQueryHandler.handle(ObtenerCopiaQuery.builder().idCopia(id).build());
        if (copia == null) {
            model.addAttribute("errorEditar", "Copia inexistente: " + id);
            return "redirect:/admin/copias";
        }
        model.addAttribute("admin", usuario);
        model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);
        var libros = listarLibrosQueryHandler.handle(new ListarLibrosQuery());
        model.addAttribute("libros", libros);
        model.addAttribute("estados", new EstadoCopia[]{EstadoCopia.EnBiblioteca, EstadoCopia.EnReparacion});
            model.addAttribute("copiaForm", CopiaFormDto.builder().id(copia.getId()).idLibro(copia.getIdLibro()).estado(copia.getEstado()).build());
        model.addAttribute("formMode", "editar");
        return "admin/admin-copia-form-editar";
    }
}
