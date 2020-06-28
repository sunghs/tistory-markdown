### [SPRING BOOT] 스프링부트로 개발하기 5 - 초기화 메소드 지정 (PostConstruct Annotation)

Component 클래스는 Autowired시 (의존성 주입 후) 초기화 메소드를 실행할 수 있다. 
이 메소드는 다른 곳에서 호출이 없어도 무조건 실행된다. 
생성자가 호출되었을 때 bean이 초기화 되지 않은 상태인데, PostConstruct 어노테이션을 사용하면 bean이 초기화 되며 동시에 의존성 확인이 가능하다. 
####  

사용조건
- 인자값이 존재하면 안된다. 파라미터가 존재하는 경우 에러가 발생한다. (무슨 값을 넣어야 할지 모르기 때문) 
- 리턴타입이 있어도 실행된다. 다만 리턴받은 값을 사용할 방법이 없다. 
- 하나의 클래스에 여러개의 PostConstruct가 있어도 된다. 
####  

```java
package sunghs.boot;
 
import javax.annotation.PostConstruct;
 
import org.springframework.stereotype.Component;
 
public @Component class InitClass {
     
    @PostConstruct
    public String init() {
        System.out.println("PostConstruct!");
        System.out.println("PostConstruct!");
        System.out.println("PostConstruct!");
        System.out.println("PostConstruct!");
        return "S";
    }
     
    @PostConstruct
    public void init2() {
        System.out.println("PostConstruct2!");
        System.out.println("PostConstruct2!");
        System.out.println("PostConstruct2!");
        System.out.println("PostConstruct2!");
    }
}
```
이러한 클래스가 있을 때 init, init2 메소드에 PostConstruct 어노테이션이 설정됨.
####  

```java
package sunghs.boot;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
 
@SpringBootApplication
public class BootExApplication implements CommandLineRunner {
     
    @Autowired
    private InitClass initClass;
     
     
    public static void main(String[] args) {
        SpringApplication.run(BootExApplication.class, args);
    }
 
    @Override
    public void run(String... args) throws Exception {
         
    }
}
```
InitClass를 Autowired만 했을 뿐 사용되는 곳이 없음
하지만 PostConstruct는 Autowired시 실행됨.
####  

#### Console
```
  .   ____          _            __ _ _ 
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \ 
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \ 
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) ) 
  '  |____| .__|_| |_|_| |_\__, | / / / / 
 =========|_|==============|___/=/_/_/_/ 
 :: Spring Boot ::  (v2.2.0.BUILD-SNAPSHOT) 

2019-07-16 16:12:16.345  INFO 69044 --]
2019-07-16 16:12:16.350  INFO 69044 --- [           main] sunghs.boot.BootExApplication            : No active profile set, falling back to default profiles: default 
PostConstruct! 
PostConstruct! 
PostConstruct! 
PostConstruct! 
PostConstruct2! 
PostConstruct2! 
PostConstruct2! 
PostConstruct2! 
Loading class `com.mysql.jdbc.Driver'. This is deprecated. The new driver class is `com.mysql.cj.jdbc.Driver'. 

The driver is automatically registered via the SPI and manual loading of the driver class is generally unnecessary. 
2019-07-16 16:12:17.071  INFO 69044 --- [           main] sunghs.boot.BootExApplication            : Started BootExApplication in 1.07 seconds (JVM running for 3.554)
```