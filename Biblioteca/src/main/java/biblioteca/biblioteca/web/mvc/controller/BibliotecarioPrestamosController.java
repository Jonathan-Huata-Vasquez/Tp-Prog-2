package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.application.command.PrestarCopiaCommand;
import biblioteca.biblioteca.application.command.PrestarCopiaCommandHandler;
import biblioteca.biblioteca.application.command.DevolverCopiaCommand;
import biblioteca.biblioteca.application.command.DevolverCopiaCommandHandler;
import biblioteca.biblioteca.application.query.ListarPrestamosQuery;
import biblioteca.biblioteca.application.query.ListarPrestamosQueryHandler;
import biblioteca.biblioteca.application.query.ValidarPrestamoQuery;
import biblioteca.biblioteca.application.query.ValidarPrestamoQueryHandler;
import biblioteca.biblioteca.application.query.ValidarDevolucionQuery;
import biblioteca.biblioteca.application.query.ValidarDevolucionQueryHandler;
import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import biblioteca.biblioteca.web.helper.ControllerHelper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/bibliotecario")
@RequiredArgsConstructor
@Slf4j
public class BibliotecarioPrestamosController {


    @GetMapping("/prestamos/nuevo")
    public String registrarPrestamoNuevo(@RequestParam(value = "idLector", required = false) Integer idLector,
                                        @RequestParam(value = "idCopia", required = false) Integer idCopia,
                                        @AuthenticationPrincipal UsuarioDetalles usuario, 
                                        HttpSession session, 
                                        Model model) {
        // Inicializar command con parámetros si existen
        var command = PrestarCopiaCommand.builder()
                .idLector(idLector)
                .idCopia(idCopia)
                .build();
        
        model.addAttribute("registrarPrestamoCommand", command);
        model.addAttribute("fechaHoy", java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAttribute("fechaVencimiento", java.time.LocalDate.now().plusDays(21).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        
        // Estado del formulario
        model.addAttribute("validado", false);
        model.addAttribute("lectorValido", false);
        model.addAttribute("copiaValida", false);
        model.addAttribute("puedeRegistrar", false); // Inicialmente no puede registrar
        
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);
        return "bibliotecario/prestamo-nuevo";
    }

    @PostMapping("/prestamos/validar")
    public String validarPrestamo(@ModelAttribute("registrarPrestamoCommand") PrestarCopiaCommand command,
                                 @AuthenticationPrincipal UsuarioDetalles usuario,
                                 HttpSession session,
                                 Model model) {
        log.debug("Validando préstamo: lector={}, copia={}", command.getIdLector(), command.getIdCopia());
        
        // IMPORTANTE: Conservar el command con los valores originales
        model.addAttribute("registrarPrestamoCommand", command);
        model.addAttribute("fechaHoy", java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAttribute("fechaVencimiento", java.time.LocalDate.now().plusDays(21).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        
        // Solo validar si ambos valores están presentes
        if (command.getIdLector() == null || command.getIdCopia() == null) {
            model.addAttribute("validado", false);
            model.addAttribute("lectorValido", false);
            model.addAttribute("copiaValida", false);
            model.addAttribute("puedeRegistrar", false);
            model.addAttribute("mensajeError", "Debe completar ambos campos antes de validar");
            controllerHelper.agregarRolActualAlModelo(model, usuario, session);
            return "bibliotecario/prestamo-nuevo";
        }
        
        // Crear query para validación
        ValidarPrestamoQuery validacionQuery = ValidarPrestamoQuery.builder()
                .idLector(command.getIdLector())
                .idCopia(command.getIdCopia())
                .build();

        // Ejecutar validación usando QueryHandler
        var resultado = validarPrestamoHandler.handle(validacionQuery);

        // Estado del formulario después de validar
        model.addAttribute("validado", true);
        model.addAttribute("lectorValido", resultado.isLectorValido());
        model.addAttribute("copiaValida", resultado.isCopiaValida());
        model.addAttribute("puedeRegistrar", resultado.isPuedeRegistrar());

        if (resultado.getResumenLector() != null) {
            model.addAttribute("resumenLector", resultado.getResumenLector());
        }

        if (resultado.getResumenEjemplar() != null) {
            model.addAttribute("resumenEjemplar", resultado.getResumenEjemplar());
        }

        if (resultado.getMensajeError() != null) {
            model.addAttribute("mensajeError", resultado.getMensajeError());
        }
        
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);
        return "bibliotecario/prestamo-nuevo";
    }

    @PostMapping("/prestamos/resetear")
    public String resetearFormulario(RedirectAttributes redirectAttributes) {
        log.debug("Reseteando formulario de préstamo");
        return "redirect:/bibliotecario/prestamos/nuevo";
    }

    @PostMapping("/prestamos")
    public String procesarRegistroPrestamo(@Valid @ModelAttribute("registrarPrestamoCommand") PrestarCopiaCommand command,
                                          BindingResult bindingResult,
                                          @AuthenticationPrincipal UsuarioDetalles usuario,
                                          HttpSession session,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        log.debug("Procesando registro de préstamo: lector={}, copia={}", command.getIdLector(), command.getIdCopia());
        
        if (bindingResult.hasErrors()) {
            // Si hay errores de validación, volver al formulario
            log.warn("Errores de binding al registrar préstamo: {}", bindingResult.getAllErrors());
            model.addAttribute("fechaHoy", java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            model.addAttribute("fechaVencimiento", java.time.LocalDate.now().plusDays(21).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            model.addAttribute("validado", false);
            model.addAttribute("puedeRegistrar", false);
            controllerHelper.agregarRolActualAlModelo(model, usuario, session);
            return "bibliotecario/prestamo-nuevo";
        }
        
        // Revalidar dominio antes de ejecutar (defensa en profundidad)
        var validacionFinal = ValidarPrestamoQuery.builder()
                .idLector(command.getIdLector())
                .idCopia(command.getIdCopia())
                .build();
        var resultadoValidacion = validarPrestamoHandler.handle(validacionFinal);
        if (!resultadoValidacion.isPuedeRegistrar()) {
            log.warn("Intento de registrar sin cumplir reglas: lector={}, copia={}", command.getIdLector(), command.getIdCopia());
            model.addAttribute("registrarPrestamoCommand", command);
            model.addAttribute("validado", true);
            model.addAttribute("lectorValido", resultadoValidacion.isLectorValido());
            model.addAttribute("copiaValida", resultadoValidacion.isCopiaValida());
            model.addAttribute("puedeRegistrar", false);
            model.addAttribute("mensajeError", resultadoValidacion.getMensajeError() != null ? resultadoValidacion.getMensajeError() : "Las reglas de negocio impiden registrar el préstamo en este momento");
            if (resultadoValidacion.getResumenLector() != null) model.addAttribute("resumenLector", resultadoValidacion.getResumenLector());
            if (resultadoValidacion.getResumenEjemplar() != null) model.addAttribute("resumenEjemplar", resultadoValidacion.getResumenEjemplar());
            model.addAttribute("fechaHoy", java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            model.addAttribute("fechaVencimiento", java.time.LocalDate.now().plusDays(21).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            controllerHelper.agregarRolActualAlModelo(model, usuario, session);
            return "bibliotecario/prestamo-nuevo";
        }

        // Enriquecer comando con idUsuario para auditoría
        if (usuario != null && usuario.getIdUsuario() != null) {
            command = PrestarCopiaCommand.builder()
                    .idUsuario(usuario.getIdUsuario())
                    .idLector(command.getIdLector())
                    .idCopia(command.getIdCopia())
                    .build();
        }

        try {
            var prestamoDto = prestarCommandHandler.handle(command);
            log.info("Préstamo registrado exitosamente: {}", prestamoDto.getId());
            redirectAttributes.addFlashAttribute("mensajeExito", "Préstamo registrado exitosamente");
            return "redirect:/bibliotecario/prestamos";
        } catch (Exception e) {
            log.error("Error al registrar préstamo", e);
            model.addAttribute("mensajeError", "Error al registrar préstamo: " + e.getMessage());
            model.addAttribute("fechaHoy", java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            model.addAttribute("fechaVencimiento", java.time.LocalDate.now().plusDays(21).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            model.addAttribute("validado", true);
            model.addAttribute("puedeRegistrar", false);
            controllerHelper.agregarRolActualAlModelo(model, usuario, session);
            return "bibliotecario/prestamo-nuevo";
        }
    }

    private final ControllerHelper controllerHelper;
    private final ListarPrestamosQueryHandler listarPrestamosHandler;
    private final PrestarCopiaCommandHandler prestarCommandHandler;
    private final ValidarPrestamoQueryHandler validarPrestamoHandler;
    private final DevolverCopiaCommandHandler devolverCommandHandler;
    private final ValidarDevolucionQueryHandler validarDevolucionHandler;

    @GetMapping("/prestamos")
    public String prestamos(@RequestParam(value = "pagina", defaultValue = "0") int pagina,
                           @RequestParam(value = "tamano", defaultValue = "20") int tamanoPagina,
                           @RequestParam(value = "estado", required = false) String estadoFiltro,
                           @AuthenticationPrincipal UsuarioDetalles usuario,
                           HttpSession session,
                           Model model) {
        log.debug("BibliotecarioPrestamosController.prestamos() llamado para usuario: {}, página: {}", 
                 usuario != null ? usuario.getUsername() : "null", pagina);
        
        // Validar parámetros
        pagina = Math.max(0, pagina);
        tamanoPagina = Math.min(Math.max(5, tamanoPagina), 100); // entre 5 y 100
        
        // Obtener préstamos paginados
        var query = ListarPrestamosQuery.builder()
                .pagina(pagina)
                .tamanoPagina(tamanoPagina)
                .estadoFiltro(estadoFiltro)
                .build();
        var resultado = listarPrestamosHandler.handle(query);
        
        // Agregar datos al modelo
        model.addAttribute("paginaPrestamos", resultado.getPaginaPrestamos());
        model.addAttribute("resumen", resultado.getResumen());
        model.addAttribute("estadoFiltro", estadoFiltro);
        
        // Agregar rol actual para navbar
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);
        
        log.debug("BibliotecarioPrestamosController: retornando vista con página {}/{}, {} préstamos", 
                 pagina + 1, resultado.getPaginaPrestamos().getTotalPaginas(), 
                 resultado.getPaginaPrestamos().getContenido().size());
        return "bibliotecario/prestamos";
    }

    // === Devolución de préstamo ===
    @GetMapping("/prestamos/devolver")
    public String devolverPrestamoForm(@RequestParam(value = "idLector", required = false) Integer idLector,
                                       @RequestParam(value = "idCopia", required = false) Integer idCopia,
                                       @AuthenticationPrincipal UsuarioDetalles usuario,
                                       HttpSession session,
                                       Model model) {
        var command = DevolverCopiaCommand.builder()
                .idLector(idLector)
                .idCopia(idCopia)
                .enviarAReparacion(false)
                .build();
        model.addAttribute("devolverCopiaCommand", command);
        model.addAttribute("validadoDevolucion", false);
        model.addAttribute("puedeDevolver", false);
        model.addAttribute("fechaHoy", java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);
        return "bibliotecario/prestamo-devolver";
    }

    @PostMapping("/prestamos/devolver/validar")
    public String validarDevolucion(@ModelAttribute("devolverCopiaCommand") DevolverCopiaCommand command,
                                    @AuthenticationPrincipal UsuarioDetalles usuario,
                                    HttpSession session,
                                    Model model) {
        log.debug("Validando devolución: lector={}, copia={}", command.getIdLector(), command.getIdCopia());
        model.addAttribute("devolverCopiaCommand", command);
        model.addAttribute("fechaHoy", java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        if (command.getIdLector() == null || command.getIdCopia() == null) {
            model.addAttribute("validadoDevolucion", false);
            model.addAttribute("puedeDevolver", false);
            model.addAttribute("mensajeError", "Debe completar ambos campos antes de validar");
            controllerHelper.agregarRolActualAlModelo(model, usuario, session);
            return "bibliotecario/prestamo-devolver";
        }

        var query = ValidarDevolucionQuery.builder()
                .idLector(command.getIdLector())
                .idCopia(command.getIdCopia())
                .build();
        var resultado = validarDevolucionHandler.handle(query);

        model.addAttribute("validadoDevolucion", true);
        model.addAttribute("puedeDevolver", resultado.isPuedeDevolver());
        if (resultado.getResumenPrestamo() != null) {
            model.addAttribute("resumenPrestamo", resultado.getResumenPrestamo());
        }
        if (resultado.getMensajeError() != null) {
            model.addAttribute("mensajeError", resultado.getMensajeError());
        }
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);
        return "bibliotecario/prestamo-devolver";
    }

    @PostMapping("/prestamos/devolver/resetear")
    public String resetearDevolucion() {
        log.debug("Reseteando formulario de devolución");
        return "redirect:/bibliotecario/prestamos/devolver";
    }

    @PostMapping("/prestamos/devolver")
    public String procesarDevolucion(@Valid @ModelAttribute("devolverCopiaCommand") DevolverCopiaCommand command,
                                     BindingResult bindingResult,
                                     @AuthenticationPrincipal UsuarioDetalles usuario,
                                     HttpSession session,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        log.debug("Procesando devolución: lector={}, copia={}, reparar={}", command.getIdLector(), command.getIdCopia(), command.getEnviarAReparacion());
        if (bindingResult.hasErrors()) {
            log.warn("Errores de binding al devolver: {}", bindingResult.getAllErrors());
            model.addAttribute("fechaHoy", java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            controllerHelper.agregarRolActualAlModelo(model, usuario, session);
            return "bibliotecario/prestamo-devolver";
        }

        // Defensa en profundidad: revalidar antes de ejecutar devolución
        var validacionQuery = ValidarDevolucionQuery.builder()
                .idLector(command.getIdLector())
                .idCopia(command.getIdCopia())
                .build();
        var resultado = validarDevolucionHandler.handle(validacionQuery);
        if (!resultado.isPuedeDevolver()) {
            log.warn("Intento de devolver sin cumplir reglas: lector={}, copia={}", command.getIdLector(), command.getIdCopia());
            model.addAttribute("devolverCopiaCommand", command);
            model.addAttribute("validadoDevolucion", true);
            model.addAttribute("puedeDevolver", false);
            if (resultado.getResumenPrestamo() != null) model.addAttribute("resumenPrestamo", resultado.getResumenPrestamo());
            model.addAttribute("mensajeError", resultado.getMensajeError() != null ? resultado.getMensajeError() : "No existe préstamo activo para esos datos");
            model.addAttribute("fechaHoy", java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            controllerHelper.agregarRolActualAlModelo(model, usuario, session);
            return "bibliotecario/prestamo-devolver";
        }

        if (usuario != null && usuario.getIdUsuario() != null) {
            command = DevolverCopiaCommand.builder()
                    .idUsuario(usuario.getIdUsuario())
                    .idLector(command.getIdLector())
                    .idCopia(command.getIdCopia())
                    .enviarAReparacion(command.getEnviarAReparacion())
                    .build();
        }
        try {
            var dto = devolverCommandHandler.handle(command);
            log.info("Devolución registrada idPrestamo={} lector={} copia={}", dto.getId(), dto.getIdLector(), dto.getIdCopia());
            redirectAttributes.addFlashAttribute("mensajeExito", "Devolución registrada exitosamente");
            return "redirect:/bibliotecario/prestamos";
        } catch (Exception e) {
            log.error("Error al registrar devolución", e);
            model.addAttribute("mensajeError", "Error al registrar devolución: " + e.getMessage());
            model.addAttribute("fechaHoy", java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            controllerHelper.agregarRolActualAlModelo(model, usuario, session);
            return "bibliotecario/prestamo-devolver";
        }
    }
}