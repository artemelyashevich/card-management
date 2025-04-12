package com.elyashevich.card_manager.util;

import com.elyashevich.card_manager.entity.Role;
import com.elyashevich.card_manager.entity.User;
import com.elyashevich.card_manager.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Date;
import java.util.List;
import java.util.Map;

@UtilityClass
public class TokenUtil {

    private final String secret = "984hg493gh0439rthr0429uruj2309yh937gc763fe87t3f89723gf";

    /**
     * Extract username claims from the provided token.
     *
     * @param token The token from which to extract the username claims.
     * @return The username extracted from the token.
     */
    public static String extractEmailClaims(final String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * Get roles from the provided token.
     *
     * @param token The token from which to extract the roles.
     * @return The roles extracted from the token as a List.
     */
    @SuppressWarnings("unchecked")
    public static List<String> getRoles(final String token) {
        return getClaimsFromToken(token).get("roles", List.class);
    }

    /**
     * Generate a token for the given UserDetails.
     *
     * @param userDetails The UserDetails object to generate the token for.
     * @return The generated token.
     */
    public static String generateToken(final User userDetails, final long tokenLifeTime) {
        return createToken(userDetails, tokenLifeTime);
    }
    /**
     * Parse and retrieve claims from the provided token.
     *
     * @param token The token from which to parse and retrieve the claims.
     * @return The parsed claims from the token.
     */
    private static Claims getClaimsFromToken(final String token) {
        return Jwts
                .parserBuilder()
                .setSigningKeyResolver(new SigningKeyResolverAdapter() {
                    @Override
                    public byte[] resolveSigningKeyBytes(JwsHeader header, Claims claims) {
                        return secret.getBytes();
                    }
                })
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Generate a JWT token for the given UserDetails.
     *
     * @param userDetails The UserDetails object for which to generate the token.
     * @return The generated JWT token.
     */
    private static String createToken(final User userDetails, final Long tokenLifeTime) {
        var issuedAt = new Date();
        var expirationDate = new Date(issuedAt.getTime() + tokenLifeTime);

        return Jwts.builder()
                .setClaims(
                        Map.of(
                                "roles",
                                userDetails.getRoles().stream()
                                    .map(Role::name)
                                    .map(SimpleGrantedAuthority::new)
                                    .toList()
                        )
                )
                .setSubject(userDetails.getEmail())
                .setIssuedAt(issuedAt)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }


    public static String validate(final String token) {
        if (getClaimsFromToken(token).getSubject().isEmpty()) {
            throw new InvalidTokenException("Invalid token.");
        }
        return token;
    }
}