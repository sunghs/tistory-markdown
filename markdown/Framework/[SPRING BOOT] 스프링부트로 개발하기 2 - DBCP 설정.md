### [SPRING BOOT] 스프링부트로 개발하기 2 - DBCP 설정

#### pom.xml Dependency 세팅 
spring-boot-jdbc를 기본으로 제공한다.
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
```

#### application.properties 세팅

spring.datasource에 대한 property key를 기본적으로 제공한다. 
####  
spring.datasource.platform=mysql 
spring.datasource.url=jdbc:mysql://localhost:3306/ps
spring.datasource.username=root 
spring.datasource.password=TEST 
spring.datasource.driver-class-name=com.mysql.jdbc.Driver 
spring.datasource.sql-script-encoding=UTF-8
####  

이후 구동해본다.
***
MySQL 한정으로 MySQL 5.x 이후에 발생하는 에러인데, The server time zone value ‘KST’ is unrecognized or represents more than one time zone과 같은 에러가 뜬다면 serverTimezone=UTC를 스키마 뒤에 붙여준다.

spring.datasource.url=jdbc:mysql://localhost:3306/ps?serverTimezone=UTC 
***
Loading class `com.mysql.jdbc.Driver'. This is deprecated. The new driver class is `com.mysql.cj.jdbc.Driver'. 

The driver is automatically registered via the SPI and manual loading of the driver class is generally unnecessary. 
이 문구가 나오면 오래된 driver-class 말고 요즘꺼 쓰라고 하는건데, 무시하고 사용할 수 있다. 
***
(정상적으로 DBCP 로드 되었다고 볼 수 있음) 
####  

구동 시 에러가 발생하지 않으면 HikariCP 연결까지 완성되었고, jdbcTemplate과 같은 spring boot jdbc를 사용할 수 있다.