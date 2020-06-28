## [SPRING BOOT] 스프링부트로 개발하기 9 - Redis(NoSQL) 기본 연동

### Redis 설치가 안되어있다면 [[REDIS] Redis 설치 (for Windows 64bit)](https://sunghs.tistory.com/90)

부트에서는 dependency만 추가하면 거의 바로 쓸 수 있도록 대부분 기능을 미리 세팅해놓았다.

### pom.xml Dependency 추가
```xml
<!-- REDIS -->
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### application.properties에 기본 설정 추가
host, port만 추가 하면 바로 사용 가능하다.
이외 세부설정은 다음에 다룬다.

```properties
spring.redis.host=127.0.0.1
spring.redis.port=6379
```

### Redis와 GET/SET을 이어줄 Test용 Class
클래스 자체를 Bean으로 사용하기 위해 Component Annotation을 달아준다.
```java
package sunghs.boot.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

public @Component class RedisCommunicator {

	private @Autowired RedisTemplate<String, String> redisTemplate;
	
	public void dataSet(String k, String v) {
		redisTemplate.opsForValue().set(k, v);
	}
	
	public String dataGet(String k) {
		return redisTemplate.opsForValue().get(k);
	}
}
```

이후 dataSet 호출 시 redis에 data를 넣고, dataGet 호출시 redis에 있는 data를 가져온다.
key-value를 String으로 고정시켜 놓았으나 redis에서는 value 자료구조를 지원하므로 다음에 제네릭 등을 이용해서 사용 할 수 있도록 한다.

### 사용 (메인클래스)

```java
@SpringBootApplication @Slf4j
public class BootExApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(BootExApplication.class, args);
	}

	
	private @Autowired RedisCommunicator redisCommunicator;
	
	@Override
	public void run(String... args) throws Exception {
		redisCommunicator.dataSet("key", "test1234567890가나다라마바사아자차카타파하");
		
		String data = redisCommunicator.dataGet("key");
		String nullData = redisCommunicator.dataGet("key1234567890");
		
		log.info(data);
		log.info(nullData);
	}
}
```

---

### LOG

**2020-01-13 14:54:19.419  INFO 4768 --- [           main] sunghs.boot.BootExApplication            : test1234567890가나다라마바사아자차카타파하
2020-01-13 14:54:19.420  INFO 4768 --- [           main] sunghs.boot.BootExApplication            : null**

---

있는 key값의 경우 value를 정상적으로 가져오고,
없는 key의 경우 null을 반환한다.

