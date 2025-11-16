package biblioteca.biblioteca.infrastructure.persistence.jpa.entity;

import biblioteca.biblioteca.domain.model.Categoria;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "libro")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LibroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String titulo;

    @Column(name = "anio_publicacion", nullable = false)
    private int anioPublicacion;

    @Column(name = "autor_id", nullable = false)
    private Integer autorId;

    @Column(name = "editorial_id", nullable = false)
    private Integer editorialId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;

    @Column(name = "descripcion", nullable = false, length = 2000)
    private String descripcion;
    
    // TODO: Implementar en el futuro
    // @Column(name = "url_portada")
    // private String urlPortada;
}
