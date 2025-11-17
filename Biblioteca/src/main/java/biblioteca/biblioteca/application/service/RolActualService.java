package biblioteca.biblioteca.application.service;

import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

/**
 * Servicio de aplicación que determina el rol activo del usuario.
 * Considera tanto los roles del usuario como la selección activa en sesión.
 */
@Service
@Slf4j
public class RolActualService {
    
    private static final String SESSION_ROL_ACTIVO = "rolActivo";
    private static final String ROL_LECTOR = "LECTOR";
    private static final String ROL_BIBLIOTECARIO = "BIBLIOTECARIO";
    private static final String ROLE_PREFIX = "ROLE_";
    
    /**
     * Determina el rol activo del usuario considerando:
     * 1. Rol seleccionado en sesión (si existe)
     * 2. Prioridad por defecto si tiene múltiples roles
     * 3. Único rol si solo tiene uno
     */
    public String determinarRolActivo(UsuarioDetalles usuario, HttpSession session) {
        if (usuario == null || usuario.getAuthorities() == null) {
            log.debug("Usuario sin roles válidos, usando LECTOR por defecto");
            return "";
        }
        
        // 1. Verificar si hay rol seleccionado en sesión
        String rolEnSesion = (String) session.getAttribute(SESSION_ROL_ACTIVO);
        if (rolEnSesion != null) {
            // Verificar que el usuario aún tenga ese rol
            boolean tieneRol = usuario.getAuthorities().stream()
                .anyMatch(auth -> (ROLE_PREFIX + rolEnSesion).equals(auth.getAuthority()));
            
            if (tieneRol) {
                log.debug("Usando rol de sesión: {}", rolEnSesion);
                return rolEnSesion;
            } else {
                // El rol en sesión ya no es válido, limpiar
                session.removeAttribute(SESSION_ROL_ACTIVO);
            }
        }
        
        // 2. Determinar rol por prioridad (BIBLIOTECARIO > LECTOR)
        boolean esBibliotecario = usuario.getAuthorities().stream()
            .anyMatch(auth -> (ROLE_PREFIX + ROL_BIBLIOTECARIO).equals(auth.getAuthority()));
        
        boolean esLector = usuario.getAuthorities().stream()
            .anyMatch(auth -> (ROLE_PREFIX + ROL_LECTOR).equals(auth.getAuthority()));
        
        // Priorizar BIBLIOTECARIO si tiene ambos
        String rolDeterminado;
        if (esBibliotecario) {
            rolDeterminado = ROL_BIBLIOTECARIO;
        } else if (esLector) {
            rolDeterminado = ROL_LECTOR;
        } else {
            rolDeterminado = ROL_LECTOR; // Por defecto
        }
        
        log.debug("Rol determinado por prioridad: {}", rolDeterminado);
        return rolDeterminado;
    }
    
    /**
     * Establece el rol activo en la sesión del usuario.
     */
    public void establecerRolActivo(String rol, HttpSession session) {
        session.setAttribute(SESSION_ROL_ACTIVO, rol);
        log.debug("Rol activo establecido en sesión: {}", rol);
    }
    
    /**
     * Verifica si el usuario tiene múltiples roles disponibles.
     */
    public boolean tieneMultiplesRoles(UsuarioDetalles usuario) {
        if (usuario == null || usuario.getAuthorities() == null) {
            return false;
        }
        
        long cantidadRoles = usuario.getAuthorities().stream()
            .filter(auth -> auth.getAuthority().startsWith(ROLE_PREFIX))
            .count();
            
        return cantidadRoles > 1;
    }
}