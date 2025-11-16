package biblioteca.biblioteca.domain.model;


import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import lombok.*;

@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Libro {

    @EqualsAndHashCode.Include
    private final Integer idLibro;

    private String titulo;
    private int anioPublicacion;
    private Integer idAutor;
    private Integer idEditorial;
    private Categoria categoria;
    private String descripcion;

    // Factoría “nuevo”
    public static Libro nuevo(String titulo, int anio, Integer idAutor, Integer idEditorial, Categoria categoria, String descripcion) {
        validar(titulo, anio, idAutor, idEditorial, categoria);
        return new Libro(
                null,
                titulo.trim(),
                anio,
                idAutor,
                idEditorial,
                categoria,
                descripcion == null ? "" : descripcion.trim()
        );
    }

    // Rehidratación
    public static Libro rehidratar(Integer id, String titulo, int anio, Integer idAutor, Integer idEditorial, Categoria categoria, String descripcion) {
        if (id == null) throw new DatoInvalidoException("idLibro no puede ser null");
        validar(titulo, anio, idAutor, idEditorial, categoria);
        return new Libro(
                id,
                titulo.trim(),
                anio,
                idAutor,
                idEditorial,
                categoria,
                descripcion == null ? "" : descripcion.trim()
        );
    }

    public void actualizarMetadatos(String titulo, int anio, Integer idAutor, Integer idEditorial, Categoria categoria, String descripcion) {
        validar(titulo, anio, idAutor, idEditorial, categoria);
        this.titulo = titulo.trim();
        this.anioPublicacion = anio;
        this.idAutor = idAutor;
        this.idEditorial = idEditorial;
        this.categoria = categoria;
        this.descripcion = (descripcion == null) ? "" : descripcion.trim();
    }

    private static void validar(String titulo, int anio, Integer idAutor, Integer idEditorial, Categoria categoria) {
        if (titulo == null || titulo.trim().isEmpty()) throw new DatoInvalidoException("El título no puede ser vacío");
        if (anio <= 0) throw new DatoInvalidoException("El año debe ser positivo");
        if (idAutor == null) throw new DatoInvalidoException("idAutor es obligatorio");
        if (idEditorial == null) throw new DatoInvalidoException("idEditorial es obligatorio");
        if (categoria == null) throw new DatoInvalidoException("La categoría es obligatoria");
    }
}
