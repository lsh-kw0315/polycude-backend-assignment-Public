package prac.backendassingment.global.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import prac.backendassingment.global.util.JwtTokenUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Skip filtering for these paths
        return  path.startsWith("/api/join")
                || path.startsWith("/api/login")
                || path.startsWith("/api/refresh")
                || path.startsWith("/api/hello")
                || (path.startsWith("/api/products") && method.equals("GET"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenUtil.getJwtFromHeader(request);

        //토큰 자체가 비어있다면
        if (!StringUtils.hasText(token)) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write("{\"message\": \"인증 실패\"}");
            return;
        }

        //토큰의 유효성 검사
        try{
            jwtTokenUtil.isTokenValid(token);
        } catch (Exception jwtException){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write("{\"message\": \"유효하지 않은 Access Token 입니다.\"}");
            return;
        }

        //토큰 값으로 Authentication 을 만들어서 SecurityContextHolder 에 할당
        try {
            Claims jwtClaims = jwtTokenUtil.getClaims(token);
            Long id = Long.parseLong(jwtClaims.getSubject());

            //Spring Security 공식 문서에서 권하는 방법은 비어있는 SecurityContext 를 만든 후 그 Context 를 SecurityContextHolder 에 할당하는 것이다
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            CustomUserDetails userDetails = new CustomUserDetails(id, jwtClaims.get("role", String.class));
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        }catch (Exception e){
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write("{\"message\": \"인증 실패\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
