package biblioteca.biblioteca.domain.model;

import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Entidad de dominio: Editorial
 * Regla: nombre no vacío/espacios.
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // uso vía factorías estáticas
public class Editorial {

    @EqualsAndHashCode.Include
    @ToString.Include
    private final Integer idEditorial;

    @ToString.Include
    private String nombre;

    /* ---------- Factorías ---------- */

    public static Editorial nuevo(String nombre) {
        Editorial e = new Editorial(null, null);
        e.actualizarNombre(nombre);
        return e;
    }

    public static Editorial rehidratar(Integer idEditorial, String nombre) {
        if (idEditorial == null) throw new DatoInvalidoException("idEditorial no puede ser null al rehidratar");
        Editorial e = nuevo(nombre);
        return new Editorial(idEditorial, e.getNombre());
    }

    /* ---------- Comportamiento ---------- */

    public void actualizarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new DatoInvalidoException("El nombre de la editorial no puede ser vacío");
        }
        this.nombre = nombre.trim();
    }

    /* ---------- Validaciones ---------- */

    private static void validarId(Integer id) {
        if (id == null) throw new DatoInvalidoException("El id de la editorial no puede ser null");
    }
}

