package mx.dgtic.unam.sismus.security;

import jakarta.servlet.http.HttpServletResponse;
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

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final UsuarioRepository usuarioRepository;

    public SecurityConfiguration(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public UserDetailsServiceImpl userDetailsService() {
        return new UserDetailsServiceImpl(usuarioRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(false); // 👈 permite distinguir usuario no encontrado
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider()) // 👈 usa nuestro proveedor personalizado
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/bootstrap/**", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        .requestMatchers("/auth/**", "/register", "/recuperar-password").permitAll()
                        .requestMatchers("/usuario/**").authenticated()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .successHandler(customSuccessHandler())
                        .failureHandler(customFailureHandler()) // 👈 manejador personalizado
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((req, res, excep) -> {
                            res.sendError(HttpServletResponse.SC_FORBIDDEN);
                        })
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> response.sendRedirect("/");
    }

    @Bean
    public AuthenticationFailureHandler customFailureHandler() {
        return (request, response, exception) -> {
            String errorMessage;

            if (exception instanceof org.springframework.security.core.userdetails.UsernameNotFoundException) {
                errorMessage = "Usuario no encontrado.";
            } else if (exception instanceof org.springframework.security.authentication.BadCredentialsException) {
                errorMessage = "Contraseña incorrecta.";
            } else {
                errorMessage = "Error al iniciar sesión.";
            }

            request.getSession().setAttribute("login_error", errorMessage);
            response.sendRedirect("/login?error=true");
        };
    }
}
