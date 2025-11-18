package biblioteca.biblioteca.web.helper;

import biblioteca.biblioteca.application.service.RolActualService;
import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;

/**
 * Helper para controllers web que centraliza operaciones comunes
 * relacionadas con la determinación del rol activo del usuario.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ControllerHelper {
    
    private final RolActualService rolActualService;
    
    /**
     * Agrega el rol actual del usuario al modelo para que los templates
     * puedan mostrar el navbar correcto y elementos específicos del rol.
     * 
     * @param model Modelo de Thymeleaf
     * @param usuario Usuario autenticado
     * @param session Sesión HTTP
     */
    public void agregarRolActualAlModelo(Model model, UsuarioDetalles usuario, HttpSession session) {
        String rolActual = rolActualService.determinarRolActivo(usuario, session);
        log.debug("ControllerHelper: agregando rolActual='{}' al modelo para usuario: {}", 
                 rolActual, usuario != null ? usuario.getUsername() : "null");
        model.addAttribute("rolActual", rolActual);
        
        // También agregar si tiene múltiples roles (útil para mostrar selector)
        boolean tieneMultiplesRoles = rolActualService.tieneMultiplesRoles(usuario);
        model.addAttribute("tieneMultiplesRoles", tieneMultiplesRoles);
    }
    
    /**
     * Método sobrecargado para cuando no necesitamos toda la información extra.
     */
    public String obtenerRolActual(UsuarioDetalles usuario, HttpSession session) {
        return rolActualService.determinarRolActivo(usuario, session);
    }

    /**
     * Calcula iniciales a partir del nombre completo del usuario.
     * Reglas:
     *  - Si nombre es nulo o vacío retorna "US" (Usuario)
     *  - Toma la primera letra de los dos primeros tokens (separados por espacios)
     *  - Si sólo hay un token, usa su primera letra
     *  - Siempre devuelve mayúsculas
     *
     * @param usuario detalles autenticación
     * @return iniciales formateadas
     */
    public String calcularIniciales(UsuarioDetalles usuario) {
        if (usuario == null) return "US";
        String nombre = usuario.getNombreCompleto();
        if (nombre == null || nombre.trim().isEmpty()) return "US";
        String[] partes = nombre.trim().split("\\s+");
        String iniciales = partes[0].substring(0, 1).toUpperCase();
        if (partes.length > 1) {
            iniciales += partes[1].substring(0, 1).toUpperCase();
        }
        return iniciales;
    }

    /**
     * Variante para calcular iniciales desde un nombre directo.
     */
    public String calcularIniciales(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) return "US";
        String[] partes = nombreCompleto.trim().split("\\s+");
        String iniciales = partes[0].substring(0, 1).toUpperCase();
        if (partes.length > 1) {
            iniciales += partes[1].substring(0, 1).toUpperCase();
        }
        return iniciales;
    }
}