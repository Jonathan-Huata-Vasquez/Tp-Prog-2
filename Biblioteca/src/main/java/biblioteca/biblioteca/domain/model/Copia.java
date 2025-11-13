package biblioteca.biblioteca.domain.model;

import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.exception.ReglaDeNegocioException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Entidad de dominio: Copia
 * Reglas:
 *  - Solo puede prestarse si estado == EnBiblioteca.
 *  - Devolución:
 *      - enBuenEstado = true  → EnBiblioteca
 *      - enBuenEstado = false → EnReparacion
 *  - Se puede marcar ConRetraso si existe un préstamo activo y vencido (lo hace la capa de dominio/servicio).
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // creación/rehidratación por factorías
public class Copia {

    @EqualsAndHashCode.Include
    @ToString.Include
    private final Integer idCopia;

    @ToString.Include
    private final Integer idLibro;

    private EstadoCopia estado;

    /* ---------- Factorías ---------- */

    public static Copia nueva(Integer idLibro) {
        if (idLibro == null) throw new DatoInvalidoException("idLibro no puede ser null");
        return new Copia(null, idLibro, EstadoCopia.EnBiblioteca);
    }

    public static Copia rehidratar(Integer idCopia, Integer idLibro, EstadoCopia estado) {
        if (idCopia == null) throw new DatoInvalidoException("idCopia no puede ser null al rehidratar");
        if (idLibro == null) throw new DatoInvalidoException("idLibro no puede ser null");
        if (estado == null) throw new DatoInvalidoException("estado no puede ser null");
        return new Copia(idCopia, idLibro, estado);
    }

    public boolean esPrestable() {
        return this.estado == EstadoCopia.EnBiblioteca;
    }

    public void marcarPrestada() {
        if (!esPrestable()) {
            throw new ReglaDeNegocioException("La copia no está disponible para préstamo (estado=" + estado + ")");
        }
        this.estado = EstadoCopia.Prestada;
    }

    public void marcarDevuelta(boolean enBuenEstado) {
        if (this.estado != EstadoCopia.Prestada && this.estado != EstadoCopia.ConRetraso) {
            throw new ReglaDeNegocioException("Solo se puede devolver una copia prestada o con retraso");
        }
        this.estado = enBuenEstado ? EstadoCopia.EnBiblioteca : EstadoCopia.EnReparacion;
    }

    /** La pone en reparación (por daño o revisión). */
    public void marcarEnReparacion() {
        this.estado = EstadoCopia.EnReparacion;
    }

    /** Disponible nuevamente en sala. */
    public void marcarDisponible() {
        this.estado = EstadoCopia.EnBiblioteca;
    }

    /** Usado por servicios cuando detectan préstamo activo vencido. */
    public void marcarConRetraso() {
        if (this.estado == EstadoCopia.Prestada) {
            this.estado = EstadoCopia.ConRetraso;
        }
    }

    /* ---------- Validaciones ---------- */
    private static void validarId(Integer id, String nombre) {
        if (id == null) throw new DatoInvalidoException(nombre + " no puede ser null");
    }
}
