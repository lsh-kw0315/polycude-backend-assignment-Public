package prac.backendassingment.global.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import prac.backendassingment.domain.model.Member;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenUtil {
    private static final String ROLE_KEY = "role";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.access-token-expire}")
    private Long ACCESS_TOKEN_EXPIRATION_TIME;

    @Value("${jwt.refresh-token-expire}")
    private Long REFRESH_TOKEN_EXPIRATION_TIME;

    private String key;

    private final SecretKey secretKey;

    public JwtTokenUtil(@Value("${jwt.key}")String key){
        //생성자 실행 시점에 @Value로 주입해줘야 한다. 기본적으로 생성자 실행 시점에는 @Value를 이용한 필드 주입 불가.
        this.key = key;
        //이 key는 base64로 인코딩 되어 있어야 함.
        byte[] keyBytes = Decoders.BASE64.decode(key);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims decodeToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token){
        try{
            decodeToken(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public Claims getClaims(String token){
        return decodeToken(token);
    }

    public String generateAccessToken(Member member) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_TIME);

        return Jwts.builder()
                .subject(member.getId().toString())
                .claim(ROLE_KEY, member.getMemberRole())
                .claim("username", member.getUsername())
                .claim("profileUrl", member.getProfileUrl())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(Long id) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_TIME);

        return Jwts.builder()
                .subject(id.toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        } else {
            return null;
        }
    }

}
