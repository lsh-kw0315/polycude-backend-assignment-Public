package prac.backendassingment.global.interceptor;

import com.sun.security.auth.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import prac.backendassingment.global.filter.CustomUserDetails;
import prac.backendassingment.global.util.JwtTokenUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StompInterceptor implements ChannelInterceptor {

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        System.out.println("커맨드: "+accessor.getCommand());
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            System.out.println("커넥트");
            System.out.println("액세서: "+accessor);
            String jwtToken = accessor.getFirstNativeHeader("Authorization");
            System.out.println("jwt 토큰: "+jwtToken);
            if(jwtToken == null || !jwtToken.startsWith("Bearer ")) throw new MessageDeliveryException("MISSING_TOKEN");

            String token = jwtToken.substring(7);
            try {
                // 토큰 유효성 검사에서 발생하는 예외를 처리합니다.
                if (jwtTokenUtil.isTokenValid(token)) {
                    Claims jwtClaims = jwtTokenUtil.getClaims(token);
                    Collection<? extends GrantedAuthority> authorities =
                            Arrays.stream(jwtClaims.get("role").toString().split(","))
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toList());

                    Long id = Long.parseLong(jwtClaims.getSubject());
                    CustomUserDetails principal = new CustomUserDetails(
                            id,
                            jwtClaims.get("role", String.class)
                    );
                    Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", authorities);
                    accessor.setUser(authentication);
                    System.out.println("스톰프 인터셉터 정상 작동");
                }
            } catch (ExpiredJwtException e) {
                // 토큰 만료 시, 클라이언트가 식별할 수 있는 에러 메시지와 함께 예외를 던집니다.
                throw new MessageDeliveryException("JWT_EXPIRED");
            } catch (Exception e) {
                // 그 외 다른 JWT 관련 예외 처리
                throw new MessageDeliveryException("INVALID_TOKEN");
            }
        }

        return message;
    }

}