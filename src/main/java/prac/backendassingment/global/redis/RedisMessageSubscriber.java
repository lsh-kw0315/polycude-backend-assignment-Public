package prac.backendassingment.global.redis;

public interface RedisMessageSubscriber {
    void handleMessage(String message);
}
