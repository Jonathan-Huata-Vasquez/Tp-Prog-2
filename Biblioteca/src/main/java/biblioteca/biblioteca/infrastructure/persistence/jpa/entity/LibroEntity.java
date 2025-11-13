package biblioteca.biblioteca.infrastructure.persistence.jpa.entity;

import biblioteca.biblioteca.domain.model.Categoria;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "libro")
public class LibroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // = idLibro

    @Column(nullable = false)
    private String titulo;

    @Column(name = "anio_publicacion", nullable = false)
    private Integer anioPublicacion;

    @Column(name = "autor_id", nullable = false)
    private Integer autorId;

    @Column(name = "editorial_id", nullable = false)
    private Integer editorialId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;
}
