package biblioteca.biblioteca.domain.port;



import biblioteca.biblioteca.domain.model.Prestamo;
import java.time.LocalDate;
import java.util.List;

public interface IPrestamoRepository {
    Prestamo guardar(Prestamo prestamo);
    Prestamo porId(Integer idPrestamo);
    Prestamo activoPor(Integer idLector, Integer idCopia);     // null si no existe
    List<Prestamo> activosPorLector(Integer idLector);
    List<Prestamo> todosLosPorLector(Integer idLector);        // incluye devueltos
    
    // Queries optimizadas para lector
    List<Prestamo> todosLosPorLectorOrdenadosPorVencimiento(Integer idLector);
    int contarActivosPorLector(Integer idLector);
    int contarVencidosPorLector(Integer idLector);
    int contarDevueltosPorLector(Integer idLector);
}