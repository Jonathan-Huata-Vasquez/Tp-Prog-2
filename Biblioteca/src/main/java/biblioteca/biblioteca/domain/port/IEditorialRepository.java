package biblioteca.biblioteca.domain.port;

import biblioteca.biblioteca.domain.model.Editorial;

import java.util.List;

public interface IEditorialRepository {
    Editorial guardar(Editorial editorial);
    Editorial porId(Integer idEditorial);
    void eliminar(Integer idEditorial);
    List<Editorial> todas();
}
