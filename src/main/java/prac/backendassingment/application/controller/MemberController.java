package prac.backendassingment.application.controller;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import prac.backendassingment.application.dto.LoginRequest;
import prac.backendassingment.application.dto.LoginResponse;
import prac.backendassingment.application.dto.MemberJoinRequest;
import prac.backendassingment.application.service.MemberService;
import prac.backendassingment.domain.model.Member;
import prac.backendassingment.global.util.JwtTokenUtil;
import prac.backendassingment.global.util.RedisTokenUtil;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenUtil jwtTokenUtil;
    private final RedisTokenUtil redisTokenUtil;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    @Value("${jwt.refresh-token-expire}")
    private Long REFRESH_TOKEN_EXPIRE;

    @Value("${cookie.secure}")
    private boolean isSecure;

    @Value("${cookie.domain}")
    private String domain;

    @PostMapping("/api/join")
    public ResponseEntity join(@RequestBody MemberJoinRequest request){
        memberService.joinMember(request);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response){
        Member member = memberService.login(request);

        String accessToken = jwtTokenUtil.generateAccessToken(member);
        String refreshToken = jwtTokenUtil.generateRefreshToken(member.getId());

        redisTokenUtil.saveRefreshToken(member.getId(), refreshToken);

        System.out.println("Saved Refresh Token: "+refreshToken);

        addRefreshTokenToCookie(response, refreshToken);

        return ResponseEntity.ok(new LoginResponse(accessToken));
    }

    @PostMapping("/api/logout")
    public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response){
        removeRefreshTokenToCookie(response);

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return ResponseEntity.ok().build();
        }

        Optional<Cookie> optional =  Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(REFRESH_TOKEN_COOKIE_NAME))
                .findFirst();

        if(optional.isEmpty()){
            return ResponseEntity.ok().build();
        }

        String refreshToken = optional.get().getValue();


        if(jwtTokenUtil.isTokenValid(refreshToken)) {
            Claims jwtClaim = jwtTokenUtil.getClaims(refreshToken);
            Long id = Long.parseLong(jwtClaim.getSubject());
            redisTokenUtil.deleteRefreshToken(id);
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/refresh")
    public ResponseEntity<LoginResponse> refresh(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if(cookies == null){
            return ResponseEntity.internalServerError().body(new LoginResponse("쿠키가 없음"));
        }

        Optional<Cookie> optional =  Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(REFRESH_TOKEN_COOKIE_NAME))
                .findFirst();

        if(optional.isEmpty()){
            return ResponseEntity.internalServerError().body(new LoginResponse("리프레시 토큰이 쿠키에 없음"));
        }

        String refreshToken = optional.get().getValue();

        if(!jwtTokenUtil.isTokenValid(refreshToken)) {
            return ResponseEntity.internalServerError().body(new LoginResponse("리프레시 토큰이 유효하지 않음"));
        }

        Claims jwtClaim = jwtTokenUtil.getClaims(refreshToken);
        Long id = Long.parseLong(jwtClaim.getSubject());
        String saved = redisTokenUtil.getRefreshToken(id);

        System.out.println("Cookie Refresh Token: "+ refreshToken);
        System.out.println("Redis Refresh Token: "+saved);

        if(!refreshToken.equals(saved)){
            redisTokenUtil.deleteRefreshToken(id);
            return ResponseEntity.internalServerError().body(new LoginResponse("서버의 리프레시 토큰과 제시된 리프레시 토큰이 다름"));
        }

        Member member = memberService.findMemberById(id);

        String newAccessToken = jwtTokenUtil.generateAccessToken(member);
        String newRefreshToken = jwtTokenUtil.generateRefreshToken(id);

        addRefreshTokenToCookie(response, newRefreshToken);

        redisTokenUtil.deleteRefreshToken(id);
        redisTokenUtil.saveRefreshToken(id, newRefreshToken);

        return ResponseEntity.ok(new LoginResponse(newAccessToken));
    }

    private void addRefreshTokenToCookie(HttpServletResponse response, String newRefreshToken) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, newRefreshToken)
                .httpOnly(true)
                .secure(isSecure)
                .sameSite(isSecure ? "None" : "Lax") // Secure가 true일 때만 None 허용
                .path("/") //쿠키를 보내는 경로
                .domain(domain != null && !domain.isEmpty() ? domain : null) // 쿠키를 보내는 URL
                .maxAge(REFRESH_TOKEN_EXPIRE/1000)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void removeRefreshTokenToCookie(HttpServletResponse response){
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .path("/") //쿠키를 보내는 경로
                .maxAge(0)
                .secure(isSecure) // 쿠키를 보내는 URL
                .domain(domain != null && !domain.isEmpty() ? domain : null)
                .sameSite(isSecure ? "None" : "Lax") // Secure가 true일 때만 None 허용
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

}
