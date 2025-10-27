package mx.dgtic.unam.sismus.security;

import mx.dgtic.unam.sismus.repository.UsuarioRepository;
import mx.dgtic.unam.sismus.security.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import java.time.LocalDateTime;

/**
 * Configuración central de seguridad para SISMUS.
 * Define el flujo de autenticación, las rutas publicas y protegidas,
 * y el manejo de login/logout con redirecciones personalizadas.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final UsuarioRepository usuarioRepository;

    public SecurityConfiguration(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Datos del usuario
    @Bean
    public UserDetailsServiceImpl userDetailsService() {
        return new UserDetailsServiceImpl(usuarioRepository);
    }

    // Codificador de contraseña
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // Proveedor de autenticacion
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }

    // Configuracion del filtro de seguridad
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider())

                // Autorizaciones
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/bootstrap/**", "/css/**", "/js/**", "/images/**", "/favicon.ico"
                        ).permitAll()
                        .requestMatchers("/login", "/register", "/recuperar-password").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                // Login
                .formLogin(login -> login
                        .loginPage("/login")
                        .permitAll()
                        .successHandler(customSuccessHandler())
                        .failureHandler(customFailureHandler())
                )

                // Logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )

                // CSRF
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));

        return http.build();
    }

    // Exito login
    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {
            String nickname = authentication.getName();
            usuarioRepository.findByNickname(nickname).ifPresent(usuario -> {
                usuario.setUltimoAcceso(LocalDateTime.now());
                usuarioRepository.save(usuario);
            });
            response.sendRedirect("/");
        };
    }

    //Error login
    @Bean
    public AuthenticationFailureHandler customFailureHandler() {
        return (request, response, exception) -> {
            String errorMessage;

            if (exception instanceof UsernameNotFoundException) {
                errorMessage = "Usuario no encontrado.";
            } else if (exception instanceof BadCredentialsException) {
                errorMessage = "Contraseña incorrecta.";
            } else if (exception instanceof DisabledException) {
                errorMessage = "Usuario inactivo. Contacte al administrador.";
            } else {
                errorMessage = "Error al iniciar sesión.";
            }

            request.getSession().setAttribute("login_error", errorMessage);
            response.sendRedirect("/login?error=true");
        };
    }

}
