package net.olegov.webfluxsecurity.security;

import static net.olegov.webfluxsecurity.utils.CommonUtils.generateAuthException;
import static net.olegov.webfluxsecurity.utils.CommonUtils.toBase64String;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.olegov.webfluxsecurity.constants.ErrorCode;
import net.olegov.webfluxsecurity.entity.UserEntity;
import net.olegov.webfluxsecurity.exception.AuthException;
import net.olegov.webfluxsecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Integer expirationInSeconds;
    @Value("${jwt.issuer}")
    private String issuer;

    private TokenDetails generateToken(UserEntity user) {
        Map<String, Object> claims = new HashMap<>() {{
            put("role", user.getRole());
            put("username", user.getUsername());
        }};

        return generateToken(claims, user.getId().toString());
    }

    private TokenDetails generateToken(Map<String, Object> claims, String subject) {
        long expirationDateTimeInMS = expirationInSeconds * 1000L;
        Date expirationDate = new Date(new Date().getTime() + expirationDateTimeInMS);

        return generateToken(expirationDate, claims, subject);
    }

    private TokenDetails generateToken(Date expirationDate, Map<String, Object> claims, String subject) {
        Date createdDate = new Date();
        String token = Jwts.builder()
                           .setClaims(claims)
                           .setIssuer(issuer)
                           .setSubject(subject)
                           .setIssuedAt(createdDate)
                           .setId(UUID.randomUUID().toString())
                           .setExpiration(expirationDate)
                           .signWith(SignatureAlgorithm.HS256, toBase64String(secret.getBytes()))
                           .compact();

        return TokenDetails.builder()
                           .token(token)
                           .issuedAt(createdDate)
                           .expiresAt(expirationDate)
                           .build();
    }

    public Mono<TokenDetails> authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                             .flatMap(user -> {
                                 if (!user.isEnabled()) {
                                     return Mono.error(generateAuthException(ErrorCode.ACCOUNT_IS_DISABLED));
                                 }

                                 if (!passwordEncoder.matches(password, user.getPassword())) {
                                     return Mono.error(generateAuthException(ErrorCode.INVALID_PASSWORD));
                                 }

                                 return Mono.just(generateToken(user).toBuilder()
                                                                     .userId(user.getId())
                                                                     .build());
                             })
                             .switchIfEmpty(Mono.error(generateAuthException(ErrorCode.INVALID_USERNAME)));
    }

}
