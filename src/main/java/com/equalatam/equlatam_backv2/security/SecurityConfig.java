package com.equalatam.equlatam_backv2.security;

import com.equalatam.equlatam_backv2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            var user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

            var authorities = Stream.concat(
                    user.getRoles().stream()
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName())),
                    user.getRoles().stream()
                            .flatMap(r -> r.getPermissions().stream())
                            .map(p -> new SimpleGrantedAuthority(p.getName()))
            ).collect(Collectors.toList());

            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(), user.getPassword(), authorities
            );
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/tracking/public/**").permitAll()
                        .requestMatchers("/api/clientes/me").authenticated()
                        .requestMatchers(HttpMethod.POST,  "/api/pedidos").hasAnyRole("CLIENTE","ADMIN")
                        .requestMatchers(HttpMethod.GET,   "/api/pedidos/cliente/**").hasAnyRole("CLIENTE","ADMIN")
                        .requestMatchers(HttpMethod.POST,  "/api/financiero/cotizaciones/*/aprobar-cliente").hasAnyRole("CLIENTE","ADMIN")
                        .requestMatchers(HttpMethod.GET,   "/api/financiero/cotizaciones/*/cliente/**").hasAnyRole("CLIENTE","ADMIN")
                        .requestMatchers(HttpMethod.GET,   "/api/financiero/facturas/cliente/**").hasAnyRole("CLIENTE","ADMIN")
                        .requestMatchers(HttpMethod.GET,   "/api/tracking/**").hasAnyRole("CLIENTE","ADMIN","SUPERVISOR","CAJERO")
                        .requestMatchers(HttpMethod.GET, "/api/financiero/cotizaciones/cliente/**").hasAnyRole("CLIENTE","ADMIN")
                        .requestMatchers("/api/financiero/**").hasAnyRole("CAJERO","ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/admin/**").hasAnyRole("CAJERO","ADMIN")
                        .requestMatchers("/api/despachos/**").hasAnyRole("SUPERVISOR","ADMIN")
                        .requestMatchers("/api/guias/**").hasAnyRole("SUPERVISOR","ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/pedidos/*/estado").hasAnyRole("SUPERVISOR","ADMIN")
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/roles/**").hasRole("ADMIN")
                        .requestMatchers("/api/permissions/**").hasRole("ADMIN")
                        .requestMatchers("/api/clientes/**").hasAnyRole("ADMIN","CAJERO")
                        .requestMatchers(HttpMethod.GET, "/api/sucursales/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/sucursales/**").hasAnyRole("ADMIN","SUPERVISOR")
                        .requestMatchers(HttpMethod.PUT, "/api/sucursales/**").hasAnyRole("ADMIN","SUPERVISOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/sucursales/**").hasAnyRole("ADMIN","SUPERVISOR")
                        .requestMatchers("/api/pedidos/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}