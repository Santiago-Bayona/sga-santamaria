package co.edu.uniquindio.sga.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Lee el header Authorization, valida el JWT y deja el usuario en el contexto de seguridad de esa peticion.
 */

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioJpaRepository usuarios;

    public JwtAuthenticationFilter(JwtService jwtService, UsuarioJpaRepository usuarios) {
        this.jwtService = jwtService;
        this.usuarios = usuarios;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            jwtService.validar(header.substring(7)).ifPresent(principal -> usuarios.findById(principal.username())
                    .filter(UsuarioJpaEntity::isActivo)
                    .ifPresent(usuario -> {
                        var auth = new UsernamePasswordAuthenticationToken(
                                usuario.getUsername(),
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }));
        }
        filterChain.doFilter(request, response);
    }
}
