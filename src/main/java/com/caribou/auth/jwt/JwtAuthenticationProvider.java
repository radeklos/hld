package com.caribou.auth.jwt;

import com.caribou.auth.JwtSettings;
import com.caribou.company.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtSettings jwtSettings;

    @Autowired
    public JwtAuthenticationProvider(JwtSettings jwtSettings) {
        this.jwtSettings = jwtSettings;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        RawAccessJwtToken rawAccessToken = (RawAccessJwtToken) authentication.getCredentials();
        Jws<Claims> jwsClaims = rawAccessToken.parseClaims(jwtSettings.getTokenSigningKey());
        Claims claimsBody = jwsClaims.getBody();
        List<String> scopes = claimsBody.get(JwtClaims.SCOPES, List.class);
        List<GrantedAuthority> authorities = scopes.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        UserContext.Builder contextBuilder = UserContext.builder()
                .username(claimsBody.getSubject())
                .authorities(authorities);
        String companyId = claimsBody.get(JwtClaims.COMPANY, String.class);
        if (companyId != null) {
            contextBuilder.companyId(UUID.fromString(claimsBody.get(JwtClaims.COMPANY, String.class)));
        }
        String roleInCompany = claimsBody.get(JwtClaims.ROLE_IN_COMPANY, String.class);
        if (companyId != null) {
            contextBuilder.roleInCompany(Role.valueOf(roleInCompany));
        }
        UserContext context = contextBuilder.build();
        return new JwtAuthenticationToken(context, context.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
