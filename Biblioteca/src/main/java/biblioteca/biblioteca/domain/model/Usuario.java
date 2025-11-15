package biblioteca.biblioteca.domain.model;

import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.exception.ReglaDeNegocioException;
import lombok.*;

import java.util.*;
import java.util.regex.Pattern;

@Getter
@ToString // toString a nivel de clase (no por atributo)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Usuario {

    @EqualsAndHashCode.Include
    private final Integer id;

    private String nombreCompleto;
    private String dni;
    private String email;         // almacenado en minúsculas
    private String passwordHash;  // hash (BCrypt/Argon2), nunca el password plano
    private final Set<Rol> roles;
    private final Integer lectorId; // nullable

    // --- Regex email (suficientemente estricta y práctica, no RFC completo) ---
    // - Local-part: letras, dígitos y . _ % + - (al menos 1)
    // - Arroba obligatoria
    // - Dominio: etiquetas con letras/dígitos y guiones, separadas por puntos
    // - TLD: 2+ letras
    private static final Pattern EMAIL_REGEX = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE
    );

    /* ===== Factorías ===== */

    public static Usuario nuevo(String nombreCompleto, String dni, String email, String passwordHash,
                                Set<Rol> roles, Integer lectorId) {
        var nombre = validarNombre(nombreCompleto);
        var ndni   = validarDniAR(dni);
        var mail   = validarYNormalizarEmail(email);
        var hash   = validarHash(passwordHash);
        var rls    = validarRoles(roles);
        return new Usuario(null, nombre, ndni, mail, hash, new HashSet<>(rls), lectorId);
    }

    public static Usuario rehidratar(Integer id, String nombreCompleto, String dni, String email,
                                     String passwordHash, Set<Rol> roles, Integer lectorId) {
        if (id == null) throw new DatoInvalidoException("id no puede ser null al rehidratar");
        var nombre = validarNombre(nombreCompleto);
        var ndni   = validarDniAR(dni);
        var mail   = validarYNormalizarEmail(email);
        var hash   = validarHash(passwordHash);
        var rls    = validarRoles(roles);
        return new Usuario(id, nombre, ndni, mail, hash, new HashSet<>(rls), lectorId);
    }

    /* ===== Comportamiento ===== */

    public void actualizarNombreCompleto(String nuevo) {
        this.nombreCompleto = validarNombre(nuevo);
    }

    public void actualizarEmail(String nuevoEmail) {
        this.email = validarYNormalizarEmail(nuevoEmail);
    }

    public void actualizarPasswordHash(String nuevoHash) {
        this.passwordHash = validarHash(nuevoHash);
    }

    public boolean tieneRol(Rol r) {
        return roles.contains(Objects.requireNonNull(r));
    }

    public void actualizarRoles(Set<Rol> nuevos) {
        if (nuevos == null || nuevos.isEmpty()) {
            throw new ReglaDeNegocioException("El usuario debe tener al menos un rol");
        }
        if (nuevos.contains(null)) {
            throw new DatoInvalidoException("Los roles no pueden contener valores null");
        }
        roles.clear();          // 'roles' es el mismo Set (final), pero mutable
        roles.addAll(nuevos);   // queda exactamente la colección solicitada
    }

    /** Regla: un usuario debe tener al menos un rol. */
    public void quitarRol(Rol r) {
        Objects.requireNonNull(r);
        if (!roles.contains(r)) return;
        if (roles.size() == 1) {
            throw new ReglaDeNegocioException("El usuario debe tener al menos un rol");
        }
        roles.remove(r);
    }

    /* ===== Validaciones ===== */

    private static String validarNombre(String v) {
        if (v == null || v.trim().isEmpty())
            throw new DatoInvalidoException("El nombre completo no puede ser vacío");
        return v.trim();
    }

    /** DNI (Argentina): 7–8 dígitos, solo números, sin puntos ni guiones. */
    private static String validarDniAR(String v) {
        if (v == null || v.trim().isEmpty())
            throw new DatoInvalidoException("El DNI no puede ser vacío");
        String t = v.trim();
        if (!t.chars().allMatch(Character::isDigit))
            throw new DatoInvalidoException("El DNI debe contener solo dígitos (sin puntos ni guiones)");
        if (t.length() != 8)
            throw new DatoInvalidoException("El DNI debe tener 8 dígitos");
        return t;
    }

    private static String validarYNormalizarEmail(String v) {
        if (v == null || v.trim().isEmpty())
            throw new DatoInvalidoException("El email no puede ser vacío");
        String e = v.trim();
        if (!EMAIL_REGEX.matcher(e).matches())
            throw new DatoInvalidoException("Email inválido");
        return e.toLowerCase(Locale.ROOT);
    }

    private static String validarHash(String v) {
        if (v == null || v.isEmpty())
            throw new DatoInvalidoException("passwordHash no puede ser vacío");
        return v;
    }

    private static Set<Rol> validarRoles(Set<Rol> roles) {
        if (roles == null || roles.isEmpty())
            throw new ReglaDeNegocioException("El usuario debe tener al menos un rol");
        if (roles.contains(null))
            throw new DatoInvalidoException("Los roles no pueden contener valores null");
        return roles;
    }
}
