package gift.service.auth;

import gift.config.properties.JwtProperties;
import gift.model.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    public String generateToken(Member member) {
        return Jwts.builder()
                .subject(member.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.expiredTime()))
                .signWith(Keys.hmacShaKeyFor(jwtProperties.secretKey().getBytes()))
                .compact();
    }

    public Long getMemberIdWithToken(String jwt) {
        var claims = decryptToken(jwt);
        return Long.parseLong(claims.getSubject());
    }

    private Claims decryptToken(String jwt) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtProperties.secretKey().getBytes()))
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }
}
