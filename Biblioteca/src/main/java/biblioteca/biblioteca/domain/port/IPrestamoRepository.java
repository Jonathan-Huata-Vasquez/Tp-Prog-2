package biblioteca.biblioteca.domain.port;



import biblioteca.biblioteca.domain.model.Prestamo;
import java.util.List;

public interface IPrestamoRepository {
    Prestamo guardar(Prestamo prestamo);
    Prestamo porId(Integer idPrestamo);
    Prestamo activoPor(Integer idLector, Integer idCopia);     // null si no existe
    List<Prestamo> activosPorLector(Integer idLector);
}