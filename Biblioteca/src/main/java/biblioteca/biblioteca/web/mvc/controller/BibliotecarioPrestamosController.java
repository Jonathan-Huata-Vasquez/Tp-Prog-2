package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.application.query.ListarPrestamosQuery;
import biblioteca.biblioteca.application.query.ListarPrestamosQueryHandler;
import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import biblioteca.biblioteca.web.helper.ControllerHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

/**
 * Controller para la gestión de préstamos del bibliotecario.
 * Maneja las funcionalidades específicas del rol bibliotecario para préstamos.
 */
@Controller
@RequestMapping("/bibliotecario")
@RequiredArgsConstructor
@Slf4j
public class BibliotecarioPrestamosController {

    private final ControllerHelper controllerHelper;
    private final ListarPrestamosQueryHandler listarPrestamosHandler;

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
}