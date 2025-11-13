package biblioteca.biblioteca.domain.model;


import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * Entidad de dominio: Autor
 * Regla: nombre no vacío/espacios, fechaNacimiento no futura, nacionalidad no vacía.
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // uso vía factorías estáticas
public class Autor {

    @EqualsAndHashCode.Include
    @ToString.Include
    private final Integer idAutor;

    @ToString.Include
    private String nombre;

    private LocalDate fechaNacimiento;

    private String nacionalidad;

    /* ---------- Factorías ---------- */

    /** Nuevo (sin id) */
    public static Autor nuevo(String nombre, LocalDate fechaNacimiento, String nacionalidad) {
        Autor a = new Autor(null, null, null, null);
        a.actualizarDatos(nombre, fechaNacimiento, nacionalidad);
        return a;
    }

    /** Rehidratación desde BD (aplica mismas validaciones de consistencia). */
    public static Autor rehidratar(Integer idAutor, String nombre, LocalDate fechaNacimiento, String nacionalidad) {
        if (idAutor == null) throw new DatoInvalidoException("idAutor no puede ser null al rehidratar");
        Autor a = nuevo(nombre, fechaNacimiento, nacionalidad);
        return new Autor(idAutor, a.getNombre(), a.getFechaNacimiento(), a.getNacionalidad());
    }


    /* ---------- Comportamiento ---------- */

    public void actualizarDatos(String nombre, LocalDate fechaNacimiento, String nacionalidad) {
        this.nombre = validarNombre(nombre);
        this.fechaNacimiento = validarFecha(fechaNacimiento);
        this.nacionalidad = validarNacionalidad(nacionalidad);
    }

    /* ---------- Validaciones ---------- */

    private static void validarId(Integer id) {
        if (id == null) throw new DatoInvalidoException("El id del autor no puede ser null");
    }

    private static String validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new DatoInvalidoException("El nombre del autor no puede ser vacío");
        }
        return nombre.trim();
    }

    private static LocalDate validarFecha(LocalDate f) {
        if (f == null) throw new DatoInvalidoException("La fecha de nacimiento no puede ser null");
        if (f.isAfter(LocalDate.now())) {
            throw new DatoInvalidoException("La fecha de nacimiento no puede ser futura");
        }
        return f;
    }

    private static String validarNacionalidad(String n) {
        if (n == null || n.trim().isEmpty()) {
            throw new DatoInvalidoException("La nacionalidad no puede ser vacía");
        }
        return n.trim();
    }
}
