package com.caribou.auth.jwt;

import com.caribou.auth.jwt.exception.JwtExpiredTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;


public class RawAccessJwtToken implements JwtToken {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(RawAccessJwtToken.class);

    private String token;

    RawAccessJwtToken(String token) {
        this.token = token;
    }

    /**
     * Parses and validates JWT Token signature.
     *
     * @param signingKey configurable signing key
     * @return List fo Claims
     * @throws BadCredentialsException username or password are incorrect
     * @throws JwtExpiredTokenException token expired
     */
    Jws<Claims> parseClaims(String signingKey) throws BadCredentialsException, JwtExpiredTokenException {
        try {
            return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(this.token);
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException ex) {
            logger.error("Invalid JWT Token", ex);
            throw new BadCredentialsException("Invalid JWT token: ", ex);
        } catch (ExpiredJwtException expiredEx) {
            logger.info("JWT Token is expired", expiredEx);
            throw new JwtExpiredTokenException(this, "JWT Token expired", expiredEx);
        }
    }

    @Override
    public String getToken() {
        return token;
    }
}
