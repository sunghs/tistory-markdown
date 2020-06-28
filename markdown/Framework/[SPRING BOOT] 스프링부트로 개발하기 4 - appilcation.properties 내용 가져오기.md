### [SPRING BOOT] 스프링부트로 개발하기 4 - appilcation.properties 내용 가져오기
appilcation.properties 에 있는 설정 내용을 가져오는 방법은 두가지가 있다. 

1. Environment 클래스에서 properties를 추출 
2. @Value 어노테이션을 사용하기

####  

### Environment 클래스에서 properties를 추출
```java
package sunghs.boot;
 
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
 
@SpringBootApplication
public class BootExApplication implements CommandLineRunner {
     
    @Autowired
    private Environment environment;
     
    private final Map<String, Object> info = new HashMap<String, Object>();
     
    public static void main(String[] args) {
        SpringApplication.run(BootExApplication.class, args);
    }
 
    @Override
    public void run(String... args) throws Exception {
        for(Iterator<PropertySource<?>> it = ((AbstractEnvironment)environment).getPropertySources().iterator(); it.hasNext();) {
            PropertySource<?> propertySource = (PropertySource<?>) it.next();
            if(propertySource instanceof MapPropertySource) {
                info.putAll(((MapPropertySource)propertySource).getSource());
            }
        }
         
    }
}
```
1. org.springframework.core.env.Environment 클래스 Autowired 
2. environment 객체를 org.springframework.core.env.AbstractEnvironment로 상위타입 캐스팅을 하고 propertySource의 iterator를 이용 
3. iterator가 돌면서 HashMap 객체에 담아준다.
####  

### @Value 어노테이션을 사용하기
```java
package sunghs.boot;
 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
 
@SpringBootApplication
public class BootExApplication implements CommandLineRunner {
     
    @Value("${int.a.b.c}")
    private int i;
     
    @Value("${string.a.b.c}")
    private String s;
     
    @Value("${boolean.a.b.c}")
    private boolean b;
     
     
    public static void main(String[] args) {
        SpringApplication.run(BootExApplication.class, args);
    }
 
    @Override
    public void run(String... args) throws Exception {
         
    }
}
```
application.properties에는 
int.a.b.c=1 
string.a.b.c=test 
boolean.a.b.c=false 
와 같은 내용이 들어있어야 하며 내용이 비어있는 경우, NullPointerException이 발생한다. 
Object와 String 말고도 primitive 타입도 지정이 가능한데 WrapperClass의 parse 메소드로 캐스팅이 가능해야한다.
####  

***
application.properties의 내용이 int.a.b.c=asd 일 때

클래스에서
@Value("${int.a.b.c}") 
private int i; 
***
#### 
이렇게 하는경우 에러가 발생한다.