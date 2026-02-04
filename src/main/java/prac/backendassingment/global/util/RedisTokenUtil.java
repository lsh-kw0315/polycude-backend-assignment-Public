package prac.backendassingment.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisTokenUtil {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String REFRESH_KEY="refresh_token:";
    @Value("${jwt.refresh-token-expire}")
    private Long REFRESH_EXPIRE;

    public void saveRefreshToken(Long id, String refreshToken){
       redisTemplate.opsForValue().set(
               REFRESH_KEY+id,
               refreshToken,
               REFRESH_EXPIRE,
               TimeUnit.MILLISECONDS
       );
    }

    public String getRefreshToken(Long id){
        return String.valueOf(redisTemplate.opsForValue().get(
                REFRESH_KEY + id
        ));
    }

    public void deleteRefreshToken(Long id){
        redisTemplate.delete(REFRESH_KEY + id);
    }
}
