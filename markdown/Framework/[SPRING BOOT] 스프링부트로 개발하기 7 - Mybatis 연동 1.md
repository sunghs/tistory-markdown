### [SPRING BOOT] 스프링부트로 개발하기 7 - Mybatis 연동 1

mybatis 연동 이전에 Spring Boot JDBC 연동이 완료되어 있어야 한다.
####  

#### pom.xml에 Dependency 추가
```xml
<!-- MYBATIS -->
<dependency>
	<groupId>org.mybatis.spring.boot</groupId>
	<artifactId>mybatis-spring-boot-starter</artifactId>
	<version>1.3.2</version>
</dependency>
```
> dependency 버전은 바뀔 수 있음.
####  

#### SqlSessionFactory Bean을 구성하는 Class (Mybatis 설정 클래스)
```java
package sunghs.boot;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan(basePackages = "sunghs.boot.*")
@EnableTransactionManagement
public @Configuration class MyBatisInitializer {

	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		bean.setDataSource(dataSource);
		bean.setMapperLocations(resolver.getResources("classpath:*.xml"));
		return bean.getObject();
	}
	
	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}

```
mybatis의 SqlSessionFactory를 반환하는 메소드가 들어있는 클래스에 Configuration 어노테이션을 붙여주도록 한다 Bean 설정을 하는 class는 Configuration 클래스임을 항상 명시 해 줘야 한다.
####  

Mybatis는 SqlSession을 이용해서 쿼리 / 프로시저를 수행하고 이 SqlSession을 생성해주는 Factory가 필요하다. 그래서 위의 두개 sqlSessionFactory, sqlSessionTemplate 메소드는 반드시 필요함.
####  

@MapperScan 어노테이션의 경우 간단한 프로젝트면 자동으로 스캔해서 달아주지만 프로젝트가 커지고 패키지 여기저기에 Mapper가 있는 경우 특정 패키지부터 시작한다는 것을 명시해줘야 그부분만 스캔한다.
####  

@EnableTransactionManagement는 mybatis 트랜잭션 매니저를 어노테이션으로 관리한다는 것을 명시해주는 어노테이션임.
####  

getResource의 classpath (src/main/resources부터 시작)이하의 xml파일을 전부 다 가져온다. 폴더별로 관리하지 않으면 관리가 복잡해 지므로 보통 resources 이하에 따로 폴더를 구성하는 경우가 일반적이라 resources/sql/ 아래의 xml 파일을 가져오는경우 classpath:/sql/\*.xml로 이름을 바꾸면 된다.
####  

#### DAO (Mapper) Class
```java
package sunghs.boot.db;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

public @Mapper @Repository interface Mappers {

	public String selectTestString() throws Exception;
}

```
SQL파일 xml과 Class 간의 연결을 도와주는 클래스
####  

@Repository 어노테이션은 Component와 크게 차이가 없지만 (Autowired를 도와줌) SQLException 핸들링 등에 특화가 되어있는 DAO에 특화 되어있는 어노테이션. 매퍼 관련 된 Autowired 클래스는 Repository로 대체한다.
####  


#### 실제 SQL이 존재하는 xml파일
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="sunghs.boot.db.Mappers">
    <select id="selectTestString" resultType="String">
    	SELECT 'SUNGHS' FROM DUAL
    </select>
</mapper>
```
mapper namespace에 어떤 MapperClass가 사용하는지 명시해 줘야 한다.
다른 MapperClass가 접근해서 쓰면 안되기 때문.
####  

sql id인 selectTestString과 이름이 일치하는 메소드가 Mapper에 있어야 한다.
####  

select태그의 경우 resultType이 필요하며, Mapper의 returnType과 일치해야 한다.
특정 value object를 쓰는 경우 클래스명을 명시해 주면 컬럼명과 일치하는 변수에 바인딩 해준다.
(resultType="sunghs.boot.model.Models" 등)
####  

#### 실행
```java
package sunghs.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;
import sunghs.boot.db.Mappers;

@SpringBootApplication
public @Slf4j class BootExApplication implements CommandLineRunner {
	
	@Autowired
	private Mappers mappers;
	
	public static void main(String[] args) {
		SpringApplication.run(BootExApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("MYBATIS : " + mappers.selectTestString());
		
	}
}
```
mappers DAO Class를 주입하고, 선언된 메소드를 쓰면 xml 파일의 해당 메소드와 이름이 일치하는 sql id의 내용을 수행해준다.
####  

#### Console
2019-07-17 14:28:22.464  INFO 80008 --- [           main] sunghs.boot.BootExApplication            : Started BootExApplication in 1.344 seconds (JVM running for 3.893)
2019-07-17 14:28:22.481  INFO 80008 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2019-07-17 14:28:22.482  WARN 80008 --- [           main] com.zaxxer.hikari.util.DriverDataSource  : Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation.
2019-07-17 14:28:22.596  INFO 80008 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
**2019-07-17 14:28:22.636  INFO 80008 --- [           main] sunghs.boot.BootExApplication            : MYBATIS : SUNGHS**
2019-07-17 14:28:22.639  INFO 80008 --- [       Thread-3] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
2019-07-17 14:28:22.643  INFO 80008 --- [       Thread-3] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.
