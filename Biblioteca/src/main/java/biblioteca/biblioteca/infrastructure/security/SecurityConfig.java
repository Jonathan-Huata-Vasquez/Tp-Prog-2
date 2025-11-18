package biblioteca.biblioteca.infrastructure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
        private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final UsuarioDetallesService usuarioDetallesService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF activo para MVC; ignorado en /api/**
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))

                .authorizeHttpRequests(auth -> auth
                        // Público
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/css/**","/img/**","/webjars/**").permitAll()
                        .requestMatchers("/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/prestamo/demo").permitAll()

                        // Dashboards por rol
                        .requestMatchers("/dashboard/admin/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/dashboard/bibliotecario/**").hasRole("BIBLIOTECARIO")
                        .requestMatchers("/dashboard/lector/**").hasRole("LECTOR")

                        // Prefijo administrativo adicional (controladores bajo /admin/...) solo para administradores
                        .requestMatchers("/admin/**").hasRole("ADMINISTRADOR")

                        // Áreas específicas protegidas
                        .requestMatchers("/lector/**").hasRole("LECTOR")
                        .requestMatchers("/bibliotecario/**").hasRole("BIBLIOTECARIO")
                        .requestMatchers("/prestamos/**").authenticated()

                        // Selector de rol requiere login
                        .requestMatchers("/seleccionar-rol").authenticated()

                        // Resto autenticado
                        .anyRequest().authenticated()
                )

                // Login con página propia
                .formLogin(form -> form
                        .loginPage("/login").permitAll()
                        .defaultSuccessUrl("/", true) // Home decide a dónde ir según roles
                )

                // Logout estándar
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                )

                // IMPORTANTE:
                // - No definimos authenticationEntryPoint → no autenticado redirige a /login (formLogin default)
                // - AccessDenied (403) redirige según roles/selector
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(new RolRedirectAccessDeniedHandler())
                )

                .authenticationProvider(daoAuthProvider())
                .headers(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioDetallesService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

        @Bean
                public PasswordEncoder passwordEncoder() {
                        return new BCryptPasswordEncoder() {
                                @Override
                                public String encode(CharSequence rawPassword) {
                                        String encoded = super.encode(rawPassword);
                                            log.info("[BCRYPT] Contraseña original: '{}' | Encriptada: {}", rawPassword, encoded);
                                        return encoded;
                                }

                                @Override
                                public boolean matches(CharSequence rawPassword, String encodedPassword) {
                                        boolean result = super.matches(rawPassword, encodedPassword);
                                        log.info("[BCRYPT] Comparando contraseña: raw='{}' hash='{}' resultado={}", rawPassword, encodedPassword, result);
                                        return result;
                                }
                        };
        }
}
