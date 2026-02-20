package com.equalatam.equlatam_backv2.security;

import com.equalatam.equlatam_backv2.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Obtener header Authorization
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraer el token (quitar "Bearer ")
        final String token = authHeader.substring(7);

        // 3. Extraer username del token
        final String username;
        try {
            username = jwtService.extractUsername(token);
        } catch (Exception e) {
            // Token malformado o inválido
            filterChain.doFilter(request, response);
            return;
        }

        // 4. Validar y autenticar si no está ya autenticado
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            var userOpt = userRepository.findByUsername(username);

            if (userOpt.isPresent() && jwtService.isTokenValid(token, username)) {

                var user = userOpt.get();

                // Construir authorities desde roles y permisos
                List<SimpleGrantedAuthority> authorities = Stream.concat(
                        user.getRoles().stream()
                                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName())),
                        user.getRoles().stream()
                                .flatMap(r -> r.getPermissions().stream())
                                .map(p -> new SimpleGrantedAuthority(p.getName()))
                ).collect(Collectors.toList());

                var authToken = new UsernamePasswordAuthenticationToken(
                        username, null, authorities
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}