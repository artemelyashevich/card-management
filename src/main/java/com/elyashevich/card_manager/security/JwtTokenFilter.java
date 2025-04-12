package com.elyashevich.card_manager.security;

import com.elyashevich.card_manager.exception.InvalidTokenException;
import com.elyashevich.card_manager.util.JsonMessageProviderUtil;
import com.elyashevich.card_manager.util.SafetyExtractEmailUtil;
import com.elyashevich.card_manager.util.TokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.elyashevich.card_manager.util.TokenConstantUtil.TOKEN_PREFIX;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final int BEGIN_INDEX = 7;

    @Override
    protected void doFilterInternal(
        @NonNull final HttpServletRequest request,
        @NonNull final HttpServletResponse response,
        @NonNull final FilterChain filterChain
    ) throws ServletException, IOException {
        var header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String jwt = null;
        String email = null;
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            jwt = header.substring(BEGIN_INDEX);
            try {
                email = SafetyExtractEmailUtil.extractEmailClaims(jwt);
            } catch (InvalidTokenException e) {
                handleException(response, e.getMessage());
                return;
            }
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                var token = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    TokenUtil.getRoles(jwt).stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList()
                );
                SecurityContextHolder.getContext().setAuthentication(token);
            }
            filterChain.doFilter(request, response);
        }
    }
    private static void handleException(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        var content = JsonMessageProviderUtil.provide(message);
        response.getWriter().write(content);
    }
}