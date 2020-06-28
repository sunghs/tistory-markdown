### [SPRING BOOT] 스프링부트로 개발하기 6 - ThreadPoolExecutor 사용
SpringBoot의 @EnableAsync 어노테이션을 이용해서 Async ThreadPoolExecutor를 사용할 수 있다.
Bean 등록 자체는 java.util.concurrent.Executor를 사용하고, 메소드 단위로 비동기 Thread를 실행 시킬 수 있다. 
####  

#### ThreadPool을 관리하는 Class
```java
package sunghs.boot;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public @Configuration @EnableAsync class ThreadPoolInitializer {

	@Bean(name="executor1")
	public Executor setExecutor1() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(1);
		threadPoolTaskExecutor.setMaxPoolSize(10);
		threadPoolTaskExecutor.setQueueCapacity(20);
		threadPoolTaskExecutor.setThreadNamePrefix("executor1-");
		return threadPoolTaskExecutor;
	}
	
	@Bean(name="executor2")
	public Executor setExecutor2() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(1);
		threadPoolTaskExecutor.setMaxPoolSize(1);
		threadPoolTaskExecutor.setQueueCapacity(10);
		threadPoolTaskExecutor.setThreadNamePrefix("executor2-");
		return threadPoolTaskExecutor;
	}
}

```

사용해야 하는 어노테이션은 설정클래스임을 알려주는 @Configuration, Boot Application에서 비동기를 사용할 것이라는 @EnableAsync 어노테이션이다.
####  

#### 기본적으로 쓰는 메소드 
setCorePoolSize : ThreadPoolExecutor가 인스턴스 되면서 기본적으로 띄울 스레드 개수. 아무작업이 없어도 corePoolSize만큼 스레드가 생성됨
setMaxPoolSize : ThreadPool 최대개수, queueCapa까자 꽉 차는 경우 maxPoolSize 만큼 넓혀감.
setQueueCapacity : 스레드 대기큐, 큐카파가 꽉차면 스레드가 추가로 생성됨.
setThreadNamePrefix : 스레드에 사용할 이름, ThreadPool이 여러군데에서 관리되면 알아보기 쉽게 사용. SpringBoot의 SimpleLoggingFormat4Java에서 이 이름으로 보여준다.
setExecutor2메소드는 코어1, 맥스1, 큐10인 스레드풀로 ConcurrentThreadPool과 비슷한 개념으로 작동한다. (싱글스레드 고정)

```java
@Bean(name="ThreadPoolExecutor이름")
```
####  

returnType이 Executor인 Bean을 SpringBoot의 ThreadPoolExecutor로 등록해줌.
이후 해당 ThreadPool에 등록되어 실행 될 메소드 레벨에 @Async("ThreadPoolExecutor이름")을 사용하면 됨. 
위 클래스 예제에서는 ThreadPoolExecutor를 두개 등록했다.
####  

#### 스레드를 사용할 Class
```java
package sunghs.boot;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

public @Slf4j @Component class GoAsync {

	@Async("executor1")
	public void runExecutor1(String str) {
		log.info("EXECUTOR 1 : " + str);
		try {
			Thread.sleep(9999999);
		}
		catch(Exception e) { 
			
		}
	}
	
	@Async("executor2")
	public void runExecutor2(String str) {
		log.info("EXECUTOR 2 : " + str);
		try {
			Thread.sleep(9999999);
		}
		catch(Exception e) {
			
		}
	}
	
}

```
####  

Thread.sleep(9999999)는 하나의 스레드가 무한으로 대기하면서 테스트하려고 넣음.
다른 곳에서 runExecutor 메소드를 실행하면 Async로 잡힌다.
```java
package sunghs.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BootExApplication implements CommandLineRunner {
	
	@Autowired
	private GoAsync go;
	
	public static void main(String[] args) {
		SpringApplication.run(BootExApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		for(int i = 0; i < 20; i ++) {
			go.runExecutor1(Integer.toString(i));
		}
	}
}
```
####  

ThreadPool 풀사이즈를 예제랑 똑같이 하면 위에 실행하면 0번 쓰레드 하나만 보인다.
쓰레드가 잡히는 순서가 코어풀사이즈만큼 실행시키고 나머지는 queue에 넣어놓고, **"queue가 꽉 차면"** 스레드를 추가로 생성한다.
그래서 queue까지만 꽉 찼으므로 실제로 스레드가 더 생성되지 않음.


```java
public void run(String... args) throws Exception {
		for(int i = 0; i < 25; i ++) {
			go.runExecutor1(Integer.toString(i));
		}
	}
```
####  
실행 갯수가 25개가 되면, thread가 추가로 잡힌다.
2019-07-17 10:27:20.343  INFO 72412 --- [           main] sunghs.boot.BootExApplication            : Started BootExApplication in 1.04 seconds (JVM running for 3.411)
2019-07-17 10:27:20.354  INFO 72412 --- [    executor1-4] sunghs.boot.GoAsync                      : EXECUTOR 1 : 23
2019-07-17 10:27:20.354  INFO 72412 --- [    executor1-5] sunghs.boot.GoAsync                      : EXECUTOR 1 : 24
2019-07-17 10:27:20.354  INFO 72412 --- [    executor1-3] sunghs.boot.GoAsync                      : EXECUTOR 1 : 22
2019-07-17 10:27:20.354  INFO 72412 --- [    executor1-1] sunghs.boot.GoAsync                      : EXECUTOR 1 : 0
2019-07-17 10:27:20.354  INFO 72412 --- [    executor1-2] sunghs.boot.GoAsync                      : EXECUTOR 1 : 21
####  
1\~5번 스레드가 0, 21,22,23,24 루프의 데이터를 갖고있는게 보인다.
1\~20 루프의 값은 현재 queue에 들어가 있기 때문임

####  
***
### maxPoolSize만큼 구동되고, Queue도 꽉차게 되면
TaskRejectedException이 발생하게 된다.

```java
public void run(String... args) throws Exception {
		for(int i = 0; i < 100; i ++) {
			go.runExecutor1(Integer.toString(i));
		}
	}
```
2019-07-17 10:43:12.843  INFO 56024 --- [    executor1-8] sunghs.boot.GoAsync                      : EXECUTOR 1 : 27
2019-07-17 10:43:12.843  INFO 56024 --- [   executor1-10] sunghs.boot.GoAsync                      : EXECUTOR 1 : 29
2019-07-17 10:43:12.843  INFO 56024 --- [    executor1-7] sunghs.boot.GoAsync                      : EXECUTOR 1 : 26
2019-07-17 10:43:12.843  INFO 56024 --- [    executor1-5] sunghs.boot.GoAsync                      : EXECUTOR 1 : 24
2019-07-17 10:43:12.843  INFO 56024 --- [    executor1-9] sunghs.boot.GoAsync                      : EXECUTOR 1 : 28
2019-07-17 10:43:12.843  INFO 56024 --- [    executor1-6] sunghs.boot.GoAsync                      : EXECUTOR 1 : 25
2019-07-17 10:43:12.843  INFO 56024 --- [    executor1-2] sunghs.boot.GoAsync                      : EXECUTOR 1 : 21
2019-07-17 10:43:12.843  INFO 56024 --- [    executor1-3] sunghs.boot.GoAsync                      : EXECUTOR 1 : 22
2019-07-17 10:43:12.843  INFO 56024 --- [    executor1-1] sunghs.boot.GoAsync                      : EXECUTOR 1 : 0
2019-07-17 10:43:12.843  INFO 56024 --- [    executor1-4] sunghs.boot.GoAsync                      : EXECUTOR 1 : 23
2019-07-17 10:43:12.854 ERROR 56024 --- [           main] o.s.boot.SpringApplication               : Application run failed
####  
java.lang.IllegalStateException: Failed to execute CommandLineRunner
	at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:787) [spring-boot-2.2.0.BUILD-SNAPSHOT.jar:2.2.0.BUILD-SNAPSHOT]
	at org.springframework.boot.SpringApplication.callRunners(SpringApplication.java:768) [spring-boot-2.2.0.BUILD-SNAPSHOT.jar:2.2.0.BUILD-SNAPSHOT]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:322) [spring-boot-2.2.0.BUILD-SNAPSHOT.jar:2.2.0.BUILD-SNAPSHOT]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1226) [spring-boot-2.2.0.BUILD-SNAPSHOT.jar:2.2.0.BUILD-SNAPSHOT]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1215) [spring-boot-2.2.0.BUILD-SNAPSHOT.jar:2.2.0.BUILD-SNAPSHOT]
	at sunghs.boot.BootExApplication.main(BootExApplication.java:15) [classes/:na]
#### Caused by: org.springframework.core.task.TaskRejectedException: Executor [java.util.concurrent.ThreadPoolExecutor@52d645b1[Running, pool size = 10, active threads = 10, queued tasks = 20, completed tasks = 0]] did not accept task: org.springframework.aop.interceptor.AsyncExecutionInterceptor$$Lambda$261/1640899500@710b18a6
at org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor.submit(ThreadPoolTaskExecutor.java:344) ~[spring-context-5.2.0.BUILD-SNAPSHOT.jar:5.2.0.BUILD-SNAPSHOT]
at org.springframework.aop.interceptor.AsyncExecutionAspectSupport.doSubmit(AsyncExecutionAspectSupport.java:290) ~[spring-aop-5.2.0.BUILD-SNAPSHOT.jar:5.2.0.BUILD-SNAPSHOT]
at org.springframework.aop.interceptor.AsyncExecutionInterceptor.invoke(AsyncExecutionInterceptor.java:129) ~[spring-aop-5.2.0.BUILD-SNAPSHOT.jar:5.2.0.BUILD-SNAPSHOT]
at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) ~[spring-aop-5.2.0.BUILD-SNAPSHOT.jar:5.2.0.BUILD-SNAPSHOT]
at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:689) ~[spring-aop-5.2.0.BUILD-SNAPSHOT.jar:5.2.0.BUILD-SNAPSHOT]
at sunghs.boot.GoAsync$$EnhancerBySpringCGLIB$$8be6cf8b.runExecutor1(<generated>) ~[classes/:na]
at sunghs.boot.BootExApplication.run(BootExApplication.java:21) [classes/:na]
at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:784) [spring-boot-2.2.0.BUILD-SNAPSHOT.jar:2.2.0.BUILD-SNAPSHOT]
	... 5 common frames omitted
***
####  
이러면서 ThreadPool이 죽어버리게 된다.
queue가 꽉차지 않도록 관리를 해주거나, TaskRejectedException 발생 시 Reject-Policy 정책을 바꿔서 Abort 할 수 있도록 관리 되어야 한다.