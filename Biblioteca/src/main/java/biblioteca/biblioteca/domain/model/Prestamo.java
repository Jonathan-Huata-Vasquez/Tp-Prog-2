package biblioteca.biblioteca.domain.model;


import biblioteca.biblioteca.domain.exception.DatoInvalidoException;

import lombok.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Prestamo {

    @EqualsAndHashCode.Include
    @ToString.Include
    private final Integer idPrestamo;        // null mientras está “nuevo” sin persistir

    @ToString.Include
    private final Integer idLector;

    @ToString.Include
    private final Integer idCopia;

    private final LocalDate fechaInicio;
    private final LocalDate fechaVencimiento;
    private LocalDate fechaDevolucion;       // null = abierto

    /* ---------- Factorías ---------- */

    /** Abre un préstamo nuevo (sin id). */
    public static Prestamo abrir(Integer idLector, Integer idCopia, LocalDate inicio, LocalDate vencimiento) {
        validarObligatorio(idLector, "idLector");
        validarObligatorio(idCopia, "idCopia");
        validarObligatorio(inicio, "fechaInicio");
        validarObligatorio(vencimiento, "fechaVencimiento");
        if (vencimiento.isBefore(inicio)) {
            throw new DatoInvalidoException("La fecha de vencimiento no puede ser anterior a la de inicio");
        }
        return new Prestamo(null, idLector, idCopia, inicio, vencimiento, null);
    }

    /** Rehidrata un préstamo ya almacenado (con id). */
    public static Prestamo rehidratar(Integer idPrestamo, Integer idLector, Integer idCopia,
                                      LocalDate inicio, LocalDate vencimiento, LocalDate devolucion) {
        validarObligatorio(idPrestamo, "idPrestamo");
        if (vencimiento.isBefore(inicio)) {
            throw new DatoInvalidoException("La fecha de vencimiento no puede ser anterior a la de inicio");
        }
        return new Prestamo(idPrestamo, idLector, idCopia, inicio, vencimiento, devolucion);
        
    }

    /* ---------- Reglas ---------- */

    public boolean estaAbierto() { return fechaDevolucion == null; }

    /** Cierra el préstamo en la fecha dada. Idempotencia negativa (no permite cerrar dos veces). */
    public void cerrar(LocalDate fechaDevolucion) {
        validarObligatorio(fechaDevolucion, "fechaDevolucion");
        if (!estaAbierto()) throw new DatoInvalidoException("El préstamo ya está cerrado");
        this.fechaDevolucion = fechaDevolucion;
    }

    /** Días de atraso “al” momento indicado (si está cerrado, usa el definitivo). */
    public int diasAtrasoAl(LocalDate fechaReferencia) {
        validarObligatorio(fechaReferencia, "fechaReferencia");
        if (!estaAbierto()) return diasAtrasoDefinitivo();
        long diff = ChronoUnit.DAYS.between(fechaVencimiento, fechaReferencia);
        return (int) Math.max(0, diff);
    }

    /** Días de atraso definitivos (requiere estar cerrado). */
    public int diasAtrasoDefinitivo() {
        if (estaAbierto()) return 0;
        long diff = ChronoUnit.DAYS.between(fechaVencimiento, fechaDevolucion);
        return (int) Math.max(0, diff);
    }

    /* ---------- Helpers ---------- */
    private static void validarObligatorio(Object v, String nombre) {
        if (v == null) throw new DatoInvalidoException(nombre + " no puede ser null");
    }
}