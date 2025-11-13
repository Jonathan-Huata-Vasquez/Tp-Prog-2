package biblioteca.biblioteca.infrastructure.persistence.jpa.entity;

import biblioteca.biblioteca.domain.model.EstadoCopia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "copia")
public class CopiaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // = idCopia

    @Column(name = "libro_id", nullable = false)
    private Integer libroId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCopia estado;
}
