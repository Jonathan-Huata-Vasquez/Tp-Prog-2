package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.application.query.BibliotecarioDashboardQuery;
import biblioteca.biblioteca.application.query.BibliotecarioDashboardQueryHandler;
import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import biblioteca.biblioteca.web.dto.BibliotecarioDashboardDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller específico para dashboard del bibliotecario.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class BibliotecarioDashboardController {

    private final BibliotecarioDashboardQueryHandler dashboardQueryHandler;

    @GetMapping("/dashboard/bibliotecario")
    public String dashboard(@AuthenticationPrincipal UsuarioDetalles usuario, Model model) {
        
        log.debug("Cargando dashboard del bibliotecario para usuario: {}", 
                  usuario != null ? usuario.getNombreCompleto() : "Anónimo");

        try {
            // Crear query vacía - el handler maneja los parámetros internamente
            BibliotecarioDashboardQuery query = BibliotecarioDashboardQuery.builder().build();
            
            // Obtener datos del dashboard
            BibliotecarioDashboardDto dashboard = dashboardQueryHandler.handle(query);
            
            // Agregar al modelo con los nombres que espera el template
            model.addAttribute("resumen", dashboard); // Para los badges
            model.addAttribute("prestamosDestacados", dashboard.getPrestamosDestacados()); // Para la tabla
            model.addAttribute("usuario", usuario);
            
            log.debug("Dashboard cargado exitosamente: {} préstamos destacados", 
                      dashboard.getPrestamosDestacados().size());

        } catch (Exception e) {
            log.error("Error al cargar dashboard del bibliotecario", e);
            // En caso de error, el template mostrará los datos de ejemplo
            model.addAttribute("usuario", usuario);
        }

        return "dashboard-bibliotecario";
    }
}