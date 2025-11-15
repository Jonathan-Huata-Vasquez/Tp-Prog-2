package biblioteca.biblioteca.infrastructure.security;

import biblioteca.biblioteca.domain.model.Rol;
import biblioteca.biblioteca.domain.model.Usuario;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


@Getter
@ToString
public class UsuarioDetalles implements UserDetails {

    private final Integer idUsuario;
    private final String nombreCompleto;
    private final String email;
    private final String password;      // hash
    private final Integer lectorId;     // puede ser null
    private final Set<Rol> roles;

    private final Collection<? extends GrantedAuthority> authorities;

    public UsuarioDetalles(Usuario u) {
        this.idUsuario = u.getId();
        this.nombreCompleto = u.getNombreCompleto();
        this.email = u.getEmail();
        this.password = u.getPasswordHash();
        this.lectorId = u.getLectorId();
        this.roles = u.getRoles();
        this.authorities = u.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .collect(Collectors.toSet());
    }

    // ==== UserDetails ====
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return email; } // Security usa "username" = email
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked()  { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
