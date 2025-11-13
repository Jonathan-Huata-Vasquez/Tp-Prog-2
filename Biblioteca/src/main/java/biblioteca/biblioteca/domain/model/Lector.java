package biblioteca.biblioteca.domain.model;

import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.exception.ReglaDeNegocioException;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entidad de dominio: Lector
 * Invariantes:
 *  - tamaño(prestamosActivos) ≤ MAX_PRESTAMOS
 *  - no puede pedir si hoy ≤ bloqueadoHasta
 *  - no puede abrir préstamo si ya tiene un préstamo activo de la misma copia
 */


@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Lector {

    public static final int MAX_PRESTAMOS = 5;
    public static final int DIAS_PRESTAMO = 21;           // coherente con Prestamo
    public static final int PENALIZACION_POR_DIA = 2;     // días bloqueado por cada día de atraso


    @EqualsAndHashCode.Include
    @ToString.Include
    @Getter
    private final Integer idLector;               // puede ser null hasta persistir

    @ToString.Include
    @Getter
    private String nombre;

    @Getter
    @ToString.Include
    private LocalDate bloqueadoHasta;

    @ToString.Include
    private final List<Prestamo> prestamosActivos;

    // --- Constructores ---
    private Lector(Integer idLector, String nombre) {
        if (idLector == null) {
            throw new DatoInvalidoException("El id del lector no puede ser null");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new DatoInvalidoException("El nombre del lector no puede ser vacío ni con espacios al inicio/fin");
        }
        this.idLector = idLector;
        this.nombre = nombre.trim();
        this.bloqueadoHasta = null;
        this.prestamosActivos = new ArrayList<>();
    }

    public static Lector nuevo(String nombre) {
        String nombreVal = validarNombre(nombre);
        return new Lector(null, nombreVal, null, new ArrayList<>());
    }

    /** Rehidratar desde BD (con id y, opcionalmente, préstamos activos). */
    /**
     * Abre un nuevo préstamo para la copia indicada (invariante: ≤5, sin bloqueo, sin duplicados de copia).
     * Efecto: añade un Prestamo abierto a la colección interna.
     */
    public Prestamo abrirPrestamo(Integer idCopia, LocalDate hoy) {
        if (idCopia == null) {
            throw new DatoInvalidoException("El id de la copia no puede ser null");
        }
        if (!puedePedir(hoy)) {
            if (prestamosActivos.size() >= MAX_PRESTAMOS) {
                throw new ReglaDeNegocioException("El lector alcanzó el máximo de " + MAX_PRESTAMOS + " préstamos activos");
            }
            throw new ReglaDeNegocioException("El lector se encuentra bloqueado hasta " + bloqueadoHasta);
        }
        if (tienePrestamoActivoDeCopia(idCopia)) {
            throw new ReglaDeNegocioException("Ya existe un préstamo activo para la misma copia (" + idCopia + ")");
        }

        // Crear préstamo abierto coherente con la regla de 21 días
        LocalDate vencimiento = hoy.plusDays(DIAS_PRESTAMO);
        Prestamo nuevo = Prestamo.abrir( this.idLector, idCopia, hoy, vencimiento);
        prestamosActivos.add(nuevo);
        return nuevo;
    }


    // --- Reglas de elegibilidad ---
    public boolean puedePedir(LocalDate hoy) {
        Objects.requireNonNull(hoy, "La fecha de 'hoy' no puede ser null");
        boolean noBloqueado = (bloqueadoHasta == null) || hoy.isAfter(bloqueadoHasta);
        boolean bajoLimite = prestamosActivos.size() < MAX_PRESTAMOS;
        return noBloqueado && bajoLimite;
    }




    // --- Comandos del agregado Lector ---


    public void actualizarNombre(String nuevoNombre) {
        this.nombre = validarNombre(nuevoNombre);
    }

    /**
     * Registra la devolución de la copia en la fecha dada.
     * Efectos:
     *  - cierra el préstamo activo correspondiente
     *  - remueve el préstamo de la lista de activos
     *  - aplica bloqueo si hubo atraso: diasAtraso * PENALIZACION_POR_DIA
     */
    public void registrarDevolucionEn(Integer idCopia, LocalDate fecha) {
        if (idCopia == null || fecha == null) {
            throw new DatoInvalidoException("idCopia y fecha no pueden ser null");
        }
        Prestamo p = buscarPrestamoActivoDeCopia(idCopia);
        if (p == null) {
            throw new ReglaDeNegocioException("No existe préstamo activo para la copia " + idCopia);
        }

        p.cerrar(fecha); // el propio Prestamo valida que la fecha de devolución sea válida y calcula atraso
        prestamosActivos.remove(p);

        int diasAtraso = p.diasAtrasoDefinitivo();
        if (diasAtraso > 0) {
            LocalDate nuevoBloqueo = fecha.plusDays((long) diasAtraso * PENALIZACION_POR_DIA);
            // Si ya estaba bloqueado por más tiempo, conservar el mayor
            if (this.bloqueadoHasta == null || nuevoBloqueo.isAfter(this.bloqueadoHasta)) {
                this.bloqueadoHasta = nuevoBloqueo;
            }
        }
    }

    /** Azúcar: devolución con fecha de sistema. */
    public void devolver(Integer idCopia) {
        registrarDevolucionEn(idCopia, LocalDate.now());
    }
    
    public List<Prestamo> prestamosVigentes() {
        return Collections.unmodifiableList(prestamosActivos);
    }
    
    /** Factoría para rehidratar desde BD (con préstamos ACTIVOS). */
    public static Lector rehidratar(Integer idLector, String nombre, LocalDate bloqueadoHasta, List<Prestamo> activos) {
        Lector l = new Lector(idLector, nombre);
        l.bloqueadoHasta = bloqueadoHasta;
        if (activos != null) {
            if (activos.size() > MAX_PRESTAMOS) {
                throw new ReglaDeNegocioException("Un lector no puede tener más de " + MAX_PRESTAMOS + " préstamos activos");
            }
            // Validaciones básicas de coherencia
            for (Prestamo p : activos) {
                if (!p.estaAbierto()) {
                    throw new ReglaDeNegocioException("Solo pueden rehidratarse préstamos activos (sin fechaDevolucion)");
                }
                if (!idLector.equals(p.getIdLector())) {
                    throw new ReglaDeNegocioException("El préstamo del lector " + p.getIdLector() + " pertenece a otro lector (esperado=" + idLector + ")");
                }
            }
            l.prestamosActivos.addAll(activos);
        }
        return l;
    }

    // --- Helpers internos ---
    private boolean tienePrestamoActivoDeCopia(Integer idCopia) {
        return prestamosActivos.stream().anyMatch(p -> Objects.equals(p.getIdCopia(), idCopia) && p.estaAbierto());
    }

    private Prestamo buscarPrestamoActivoDeCopia(Integer idCopia) {
        return prestamosActivos.stream()
                .filter(p -> Objects.equals(p.getIdCopia(), idCopia) && p.estaAbierto())
                .findFirst()
                .orElse(null);
    }

    private static String validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new DatoInvalidoException("El nombre del lector no puede ser vacío");
        }
        return nombre.trim();
    }
}
