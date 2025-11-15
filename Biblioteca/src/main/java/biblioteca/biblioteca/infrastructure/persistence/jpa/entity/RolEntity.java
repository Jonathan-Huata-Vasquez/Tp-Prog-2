package biblioteca.biblioteca.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rol", uniqueConstraints = @UniqueConstraint(name = "uk_rol_nombre", columnNames = "nombre"))
public class RolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String nombre; // LECTOR | BIBLIOTECARIO | ADMINISTRADOR
}
