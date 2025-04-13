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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.elyashevich.card_manager.util.TokenConstantUtil.TOKEN_PREFIX;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final int TOKEN_PREFIX_LENGTH = 7;
    private static final String AUTH_ERROR_MESSAGE = "Authentication error: {}";

    @Override
    protected void doFilterInternal(
        @NonNull final HttpServletRequest request,
        @NonNull final HttpServletResponse response,
        @NonNull final FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = authHeader.substring(TOKEN_PREFIX_LENGTH);
            String email = SafetyExtractEmailUtil.extractEmailClaims(jwt);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                List<SimpleGrantedAuthority> authorities = TokenUtil.getRoles(jwt).stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    authorities
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);
        } catch (InvalidTokenException e) {
            log.warn(AUTH_ERROR_MESSAGE, e.getMessage());
            handleAuthenticationError(response, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected authentication error", e);
            handleAuthenticationError(response, "Internal authentication error");
        }
    }

    private void handleAuthenticationError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(JsonMessageProviderUtil.provide(message));
        response.getWriter().flush();
    }
}