package prac.backendassingment;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class TestContainerConfig {

    static final GenericContainer<?> REDIS_CONTAINER;
    static final MongoDBContainer MONGO_CONTAINER;

    //static 블록이 로드되자마자 실행되고 JVM 생애 주기 중에서 한 번만 로드된다는 점 이용
    static {
        // 1. 컨테이너 정의
        REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
                .withExposedPorts(6379);
        MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo:6.0"));

        // 2. 수동 시작 (컨테이너를 딱 한 번만 띄움)
        REDIS_CONTAINER.start();
        MONGO_CONTAINER.start();

        // 3. 시스템 프로퍼티 강제 주입 (스프링 로드 전 최우선 순위)
        System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost());
        System.setProperty("spring.data.redis.port", REDIS_CONTAINER.getMappedPort(6379).toString());
        System.setProperty("spring.data.mongodb.uri", MONGO_CONTAINER.getReplicaSetUrl());
    }
}