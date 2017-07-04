package com.caribou.auth.jwt;

import com.caribou.auth.JwtSettings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;


@Component
public class JwtTokenFactory {

    private final JwtSettings settings;

    @Autowired
    public JwtTokenFactory(JwtSettings settings) {
        this.settings = settings;
    }

    /**
     * Factory method for issuing new JWT Tokens.
     *
     * @param userContext user context
     */
    public AccessJwtToken createAccessJwtToken(UserContext userContext) {
        if (StringUtils.isEmpty(userContext.getUsername())) {
            throw new IllegalArgumentException("Cannot create JWT Token without username");
        }

        if (userContext.getAuthorities() == null || userContext.getAuthorities().isEmpty()) {
            throw new IllegalArgumentException("User doesn't have any privileges");
        }

        Claims claims = Jwts.claims().setSubject(userContext.getUsername());
        claims.put(JwtClaims.SCOPES, userContext.getAuthorities().stream().map(Object::toString).collect(Collectors.toList()));
        claims.put(JwtClaims.COMPANY, userContext.getCompanyId());
        claims.put(JwtClaims.ROLE_IN_COMPANY, userContext.getRoleInCompany());
        Instant currentTime = Instant.now();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(settings.getTokenIssuer())
                .setIssuedAt(Date.from(currentTime))
                .setExpiration(Date.from(currentTime.plus(settings.getTokenExpirationTime(), ChronoUnit.MINUTES)))
                .signWith(SignatureAlgorithm.HS512, settings.getTokenSigningKey())
                .compact();
        return new AccessJwtToken(token, claims);
    }

    public JwtToken createRefreshToken(UserContext userContext) {
        if (StringUtils.isEmpty(userContext.getUsername())) {
            throw new IllegalArgumentException("Cannot create JWT Token without username");
        }
        Instant currentTime = Instant.now();
        Claims claims = Jwts.claims().setSubject(userContext.getUsername());
        claims.put(JwtClaims.SCOPES, Collections.singletonList(Scopes.REFRESH_TOKEN.authority()));
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(settings.getTokenIssuer())
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(currentTime))
                .setExpiration(Date.from(currentTime.plus(settings.getTokenExpirationTime(), ChronoUnit.MINUTES)))
                .signWith(SignatureAlgorithm.HS512, settings.getTokenSigningKey())
                .compact();
        return new AccessJwtToken(token, claims);
    }

}
