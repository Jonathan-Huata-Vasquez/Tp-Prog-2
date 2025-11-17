package biblioteca.biblioteca.web.mvc.controller;

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
import java.util.List;
import java.util.ArrayList;

/**
 * Controller para gestión de lectores por bibliotecarios.
 */
@Controller
@RequestMapping("/bibliotecario")
@RequiredArgsConstructor
@Slf4j
public class BibliotecarioLectoresController {

    private final ControllerHelper controllerHelper;

    @GetMapping("/lectores")
    public String lectores(@AuthenticationPrincipal UsuarioDetalles usuario,
                          HttpSession session,
                          @RequestParam(value = "estado", required = false) String estadoFiltro,
                          @RequestParam(value = "pagina", defaultValue = "0") int pagina,
                          @RequestParam(value = "tamano", defaultValue = "20") int tamano,
                          Model model) {

        log.debug("Cargando página de lectores para bibliotecario: {}, filtro: {}, página: {}, tamaño: {}",
                  usuario != null ? usuario.getNombreCompleto() : "Anónimo", estadoFiltro, pagina, tamano);

        try {
            // Por ahora, crear datos de ejemplo para que funcione la página
            // TODO: Implementar query real cuando esté listo el backend
            
            // Crear resumen de ejemplo
            ResumenLectores resumen = new ResumenLectores(50, 45, 5);
            model.addAttribute("resumen", resumen);
            
            // Crear lista vacía de lectores por ahora
            List<Object> lectores = new ArrayList<>();
            model.addAttribute("lectores", lectores);
            model.addAttribute("estadoFiltro", estadoFiltro);
            
            // Agregar rol actual para navbar
            controllerHelper.agregarRolActualAlModelo(model, usuario, session);

            log.debug("Página de lectores cargada exitosamente");
            return "bibliotecario/lectores";

        } catch (Exception e) {
            log.error("Error al cargar página de lectores", e);
            model.addAttribute("error", "Error al cargar la página de lectores");
            return "error";
        }
    }

    // DTO temporal para el resumen
    public static class ResumenLectores {
        private final int totalLectores;
        private final int habilitados;
        private final int bloqueados;

        public ResumenLectores(int totalLectores, int habilitados, int bloqueados) {
            this.totalLectores = totalLectores;
            this.habilitados = habilitados;
            this.bloqueados = bloqueados;
        }

        public int getTotalLectores() { return totalLectores; }
        public int getHabilitados() { return habilitados; }
        public int getBloqueados() { return bloqueados; }
    }
}