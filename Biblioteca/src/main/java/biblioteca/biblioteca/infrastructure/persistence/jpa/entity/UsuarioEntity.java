package biblioteca.biblioteca.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuario",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_usuario_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_usuario_dni", columnNames = "dni")
        })
public class UsuarioEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

        @Column(nullable = false, length = 12, unique = true)
        private String dni;

        @Column(nullable = false, length = 120, unique = true)
        private String email;

    @Column(name = "password_hash", nullable = false, length = 200)
    private String passwordHash;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "usuario_rol",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id"),
            uniqueConstraints = @UniqueConstraint(name = "uk_usuario_rol", columnNames = {"usuario_id","rol_id"})
    )
    private Set<RolEntity> roles = new HashSet<>();

    @Column(name = "lector_id")
    private Integer lectorId; // nullable
}
