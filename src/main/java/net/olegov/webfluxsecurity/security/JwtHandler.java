package net.olegov.webfluxsecurity.security;

import static net.olegov.webfluxsecurity.utils.CommonUtils.generateAuthException;
import static net.olegov.webfluxsecurity.utils.CommonUtils.toBase64String;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import net.olegov.webfluxsecurity.constants.ErrorCode;
import net.olegov.webfluxsecurity.exception.UnauthorizedException;
import reactor.core.publisher.Mono;

public class JwtHandler {
    
    private final String secret;
    
    public JwtHandler(String secret) {
        this.secret = secret;
    }

    public Mono<VerificationResult> check(String accessToken) {
        return Mono.just(verify(accessToken))
            .onErrorResume(e -> Mono.error(new UnauthorizedException(e.getMessage())));
    }

    private VerificationResult verify(String token) {
        Claims claims = getClaimsFromToken(token);
        final Date expirationDate = claims.getExpiration();

        if (expirationDate.before(new Date())) {
            throw generateAuthException(ErrorCode.EXPIRED_TOKEN);
        }

        return new VerificationResult(claims, token);
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                   .setSigningKey(toBase64String(secret.getBytes()))
                   .parseClaimsJws(token)
                   .getBody();
    }

    public static class VerificationResult {
        public Claims claims;
        public String token;

        public VerificationResult(Claims claims, String token) {
            this.claims = claims;
            this.token = token;
        }

    }

}
