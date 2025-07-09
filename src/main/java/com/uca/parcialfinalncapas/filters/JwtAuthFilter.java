package com.uca.parcialfinalncapas.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uca.parcialfinalncapas.utils.GeneralUtils;
import com.uca.parcialfinalncapas.utils.JwtUtils;
import com.uca.parcialfinalncapas.utils.ResponseBuilderUtil;

@Component
@Order(0) // Asegúrate de que este filtro se ejecute antes de otros filtros de seguridad
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private final JwtUtils jwtUtils;

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final List<String> EXCLUDED_PATHS = List.of(GeneralUtils.EXCLUDED_PATHS);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (isExcluded(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = extractToken(request);

            if (token != null) {
                String email = jwtUtils.extractClaim(token, Claims::getSubject);

                if (email != null) {

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, null,
                            null);

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                } else {
                    System.out.println("Usuario no encontrado en el token JWT");
                }
            }

            filterChain.doFilter(request, response); // Continúa la cadena de filtros
        } catch (ExpiredJwtException ex) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response
                    .getWriter()
                    .write(
                            new ObjectMapper()
                                    .writeValueAsString(
                                            ResponseBuilderUtil.buildErrorResponse(ex, HttpStatus.FORBIDDEN,
                                                    "Token expirado")));
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response
                    .getWriter()
                    .write(
                            new ObjectMapper()
                                    .writeValueAsString(
                                            ResponseBuilderUtil.buildErrorResponse(ex, HttpStatus.FORBIDDEN,
                                                    "Token inválido o no autorizado")));
        }
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private boolean isExcluded(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}
