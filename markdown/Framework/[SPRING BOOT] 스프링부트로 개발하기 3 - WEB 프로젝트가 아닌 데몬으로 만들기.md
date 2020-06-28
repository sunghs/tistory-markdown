### [SPRING BOOT] 스프링부트로 개발하기 3 - WEB 프로젝트가 아닌 데몬으로 만들기

SpringBootApplication 어노테이션이 설정되어있는 부트 메인클래스에서 CommandLineRunner를 구현하면 사용할 수 있다.
####  

```java
package sunghs.boot;
 
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
 
@SpringBootApplication
public class BootExApplication {
 
    public static void main(String[] args) {
        SpringApplication.run(BootExApplication.class, args);
    }
 
}
```
CommandLineRunner interface를 implements해서, run 메소드를 override 한다.
```java
package sunghs.boot;
 
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
 
@SpringBootApplication
public class BootExApplication implements CommandLineRunner {
 
    public static void main(String[] args) {
        SpringApplication.run(BootExApplication.class, args);
    }
 
    @Override
    public void run(String... args) throws Exception {
        System.out.println("이부분에 실행할 class나 호출 할 로직을 설정하면 됨");
         
    }
 
}
```
물론 Component 클래스를 바로 Autowired 해서 사용할 수 있다.
```java
package sunghs.boot;
 
import org.springframework.stereotype.Component;
 
public @Component class Caller {
     
    public void call() {
        System.out.println("Boot CommandLineRunner Caller Call");
    }
}
```
이런 클래스가 존재할 때
```java
package sunghs.boot;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
 
@SpringBootApplication
public class BootExApplication implements CommandLineRunner {
 
    @Autowired
    private Caller caller;
     
    public static void main(String[] args) {
        SpringApplication.run(BootExApplication.class, args);
    }
 
    @Override
    public void run(String... args) throws Exception {
        System.out.println("이부분에 실행할 class나 호출 할 로직을 설정하면 됨");
         
        caller.call();
    }
 
}
```
Caller Class를 Autowired해서 사용했다. Component 클래스의 bean 등록이 CommandLineRunner 실행 시점보다 이전이므로 사용가능하다.