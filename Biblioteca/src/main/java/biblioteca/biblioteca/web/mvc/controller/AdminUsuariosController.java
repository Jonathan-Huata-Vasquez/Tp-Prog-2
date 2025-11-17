package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.application.query.ListarUsuariosQuery;
import biblioteca.biblioteca.application.query.ListarUsuariosQueryHandler;
import biblioteca.biblioteca.application.query.ObtenerUsuarioQuery;
import biblioteca.biblioteca.application.query.ObtenerUsuarioQueryHandler;
import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.application.command.CrearUsuarioCommand;
import biblioteca.biblioteca.application.command.CrearUsuarioCommandHandler;
import biblioteca.biblioteca.application.command.ActualizarUsuarioCommand;
import biblioteca.biblioteca.application.command.ActualizarUsuarioCommandHandler;
import biblioteca.biblioteca.application.command.EliminarUsuarioCommand;
import biblioteca.biblioteca.application.command.EliminarUsuarioCommandHandler;
import biblioteca.biblioteca.web.dto.UsuarioFormDto;
import biblioteca.biblioteca.domain.model.Rol;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import biblioteca.biblioteca.web.helper.ControllerHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.Set;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AdminUsuariosController {

    private final ControllerHelper controllerHelper;
    private final ListarUsuariosQueryHandler listarUsuariosQueryHandler;
    private final ObtenerUsuarioQueryHandler obtenerUsuarioQueryHandler;
    private final CrearUsuarioCommandHandler crearUsuarioCommandHandler;
    private final ActualizarUsuarioCommandHandler actualizarUsuarioCommandHandler;
    private final EliminarUsuarioCommandHandler eliminarUsuarioCommandHandler;
    // Se eliminan las constantes para usar literales directos como solicitó el usuario.

    @GetMapping("/admin/usuarios")
    public String listar(@AuthenticationPrincipal UsuarioDetalles usuario,
                         HttpSession session,
                         Model model) {
        
        // Datos para la tabla
        var usuarios = listarUsuariosQueryHandler.handle(ListarUsuariosQuery.builder().build());
        model.addAttribute("usuarios", usuarios);

        // Datos comunes para navbar
        model.addAttribute("admin", usuario);
        model.addAttribute("adminIniciales", calcularIniciales(usuario));
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);

        return "admin/admin-usuarios";
    }

    @GetMapping("/admin/usuarios/nuevo")
    public String nuevo(@AuthenticationPrincipal UsuarioDetalles usuario,
                        HttpSession session,
                        Model model) {

        model.addAttribute("admin", usuario);
        model.addAttribute("adminIniciales", calcularIniciales(usuario));
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);
        model.addAttribute("formMode", "crear");
        model.addAttribute("usuarioForm", UsuarioFormDto.builder().roles(Set.of(Rol.LECTOR)).build());
        model.addAttribute("rolesDisponibles", Rol.values());
        return "admin/admin-usuario-form";
    }

    @PostMapping("/admin/usuarios/nuevo")
    public String crear(@AuthenticationPrincipal UsuarioDetalles usuario,
                        HttpSession session,
                        Model model,
                        @Valid @ModelAttribute("usuarioForm") UsuarioFormDto form,
                        BindingResult bindingResult) {
        log.debug("POST crear usuario por: {}", usuario != null ? usuario.getUsername() : "anon");
        if (bindingResult.hasErrors()) {
            prepararModeloForm(model, usuario, session, "crear");
            model.addAttribute("rolesDisponibles", Rol.values());
            return "admin/admin-usuario-form";
        }
        var cmd = CrearUsuarioCommand.builder()
                .nombreCompleto(form.getNombreCompleto())
                .dni(form.getDni())
                .email(form.getEmail())
            .passwordHash(form.getPassword())
                .roles(form.getRoles())
                .lectorId(form.getLectorId())
                .build();
        try {
            crearUsuarioCommandHandler.handle(cmd);
        } catch (DataIntegrityViolationException e) {
            prepararModeloForm(model, usuario, session, "crear");
            model.addAttribute("rolesDisponibles", Rol.values());
            model.addAttribute("errorEmail", "Email o DNI ya registrado");
            return "admin/admin-usuario-form";
        }
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/admin/usuarios/{id}/editar")
    public String editar(@PathVariable Integer id,
                         @AuthenticationPrincipal UsuarioDetalles usuario,
                         HttpSession session,
                         Model model) {
        log.debug("Formulario editar usuario {} solicitado por: {}", id, usuario != null ? usuario.getUsername() : "anon");
        UsuarioFormDto formDto;
        try {
            var dto = obtenerUsuarioQueryHandler.handle(ObtenerUsuarioQuery.builder().idUsuario(id).build());
            formDto = UsuarioFormDto.fromDto(dto);
        } catch (EntidadNoEncontradaException e) {
            var usuarios = listarUsuariosQueryHandler.handle(ListarUsuariosQuery.builder().build());
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("errorEditar", e.getMessage());
            model.addAttribute("admin", usuario);
            model.addAttribute("adminIniciales", calcularIniciales(usuario));
            controllerHelper.agregarRolActualAlModelo(model, usuario, session);
            return "admin/admin-usuarios";
        }
        model.addAttribute("admin", usuario);
        model.addAttribute("adminIniciales", calcularIniciales(usuario));
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);
        model.addAttribute("formMode", "editar");
        model.addAttribute("usuarioForm", formDto);
        model.addAttribute("rolesDisponibles", Rol.values());
        return "admin/admin-usuario-form";
    }

    @PostMapping("/admin/usuarios/{id}/editar")
    public String actualizar(@PathVariable Integer id,
                             @AuthenticationPrincipal UsuarioDetalles usuario,
                             HttpSession session,
                             Model model,
                             @Valid @ModelAttribute("usuarioForm") UsuarioFormDto form,
                             BindingResult bindingResult) {
        log.debug("POST actualizar usuario {} por: {}", id, usuario != null ? usuario.getUsername() : "anon");
        if (bindingResult.hasErrors()) {
            prepararModeloForm(model, usuario, session, "editar");
            model.addAttribute("rolesDisponibles", Rol.values());
            return "admin/admin-usuario-form";
        }
        var cmd = ActualizarUsuarioCommand.builder()
                .idUsuario(id)
                .nombreCompleto(form.getNombreCompleto())
                .email(form.getEmail())
                .dni(form.getDni())
                .roles(form.getRoles())
                .lectorId(form.getLectorId())
                .build();
        try {
            actualizarUsuarioCommandHandler.handle(cmd);
        } catch (DataIntegrityViolationException e) {
            prepararModeloForm(model, usuario, session, "editar");
            model.addAttribute("rolesDisponibles", Rol.values());
            model.addAttribute("usuarioForm", form);
            model.addAttribute("errorEmail", "Email o DNI ya registrado");
            return "admin/admin-usuario-form";
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/admin/usuarios/{id}/eliminar")
    public String eliminar(@PathVariable Integer id,
                           @AuthenticationPrincipal UsuarioDetalles usuario,
                           HttpSession session,
                           Model model) {
        log.debug("POST eliminar usuario {} por: {}", id, usuario != null ? usuario.getUsername() : "anon");
        try {
            eliminarUsuarioCommandHandler.handle(EliminarUsuarioCommand.builder().idUsuario(id).build());
            return "redirect:/admin/usuarios";
        } catch (Exception e) { // ReglaDeNegocioException o EntidadNoEncontrada
            // Reconstruir listado con mensaje de error
            var usuarios = listarUsuariosQueryHandler.handle(ListarUsuariosQuery.builder().build());
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("errorEliminar", e.getMessage());
            model.addAttribute("admin", usuario);
            model.addAttribute("adminIniciales", calcularIniciales(usuario));
            controllerHelper.agregarRolActualAlModelo(model, usuario, session);
            return "admin/admin-usuarios";
        }
    }

    private String calcularIniciales(UsuarioDetalles usuario) {
        String iniciales = "AD"; // fallback genérico
        if (usuario != null && usuario.getNombreCompleto() != null) {
            String[] partes = usuario.getNombreCompleto().trim().split("\\s+");
            if (partes.length >= 1) {
                iniciales = partes[0].substring(0, 1).toUpperCase();
                if (partes.length >= 2) {
                    iniciales += partes[1].substring(0, 1).toUpperCase();
                }
            }
        }
        return iniciales;
    }

    private void prepararModeloForm(Model model, UsuarioDetalles usuario, HttpSession session, String modo) {
        model.addAttribute("admin", usuario);
        model.addAttribute("adminIniciales", calcularIniciales(usuario));
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);
        model.addAttribute("formMode", modo);
    }
}
