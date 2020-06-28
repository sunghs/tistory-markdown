## [SPRING BOOT] 스프링부트로 웹 개발하기 2 - MyBatis 연동

MVC 기본 모델인 Controller -> Service -> Mapper 구조로 구현

### 먼저 JDBC랑 MyBatis 사용을 위해 pom.xml 에 Dependency 추가 해 준다.
```xml
<!-- MYBATIS -->
<dependency>
	<groupId>org.mybatis.spring.boot</groupId>
	<artifactId>mybatis-spring-boot-starter</artifactId>
	<version>1.3.2</version>
</dependency>
<!-- JDBC -->
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

### SQL을 관리 할 폴더를 만든다.
꼭 하위폴더에 없어도 된다. SqlSession Config Class에 resolver가 읽을 수 있게만 해주면 된다.
예제는 resources/mapper/MainMapper.xml 이다.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="sunghs.boot.web.mapper.MainMapper">
	<select id="selectPacketStat" parameterType="String" resultType="HashMap">
		SELECT 
    		'PACKET_FROM~~' AS packetFrom,
    		'PACKET_TO~~' AS packetTo,
    		PACKET_STREAM_COUNT AS packetStreamCount
    	FROM PS_PACKET_STAT
    	WHERE PACKET_STAT_DATE = #{packetStatDate}
	</select>
</mapper>
```
namespace에 해당 mapper.xml 을 사용할 MapperClass 명을 명시해 줘야 한다.
해당 Mapper가 없거나 패키지가 다르거나 오타가 나는경우 **_invalid bound statement (not found)_** 에러가 발생한다.

### SqlSession을 만들어 줄 Config Class 를 만든다.
패키지는 임의로 만들어준다.
```java
package sunghs.boot.web.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan(basePackages = "sunghs.boot.web.mapper.*")
public @Configuration @EnableTransactionManagement class MapperConfig {
	
	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		bean.setDataSource(dataSource);
		bean.setMapperLocations(resolver.getResources("classpath:mapper/*.xml"));
		return bean.getObject();
	}
	
	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory);
	}

}
```
Bean Annotation 사용 시 반드시 해당 클래스 Configuration을 달아줘야 한다.
resolver가 가져오는 classpath 위치는 sql xml 파일이 존재하는 위치다.
MapperScan은 MapperClass가 존재하는 위치를 스캔한다.

### SQL xml 을 가져올 Mapper Class 작성
```java
package sunghs.boot.web.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

public @Mapper interface MainMapper {
 
	public List<Map<String, Object>> selectPacketStat(String PacketStatDate);
}

```
Mapper Annotation을 빼면 안된다. (Repository로 대체 불가)
Interface Bean 생성이 불가하기 때문이다.
이후 Service, ServiceImpl, Controller 등에서 Repository Mapper를 사용하게 되면 **_Field mainMapper in sunghs.boot.web.impl.MainServiceImpl required a bean of type 'sunghs.boot.web.mapper.MainMapper' that could not be found._** 와 같은 bean 생성 실패 에러가 난다.

### Mapper를 사용할 Service, ServiceImpl 작성
#### - Service Class
```java
package sunghs.boot.web.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

public @Service interface MainService {

	public List<Map<String, Object>> getPacketStat(String packetStatDate);
}
```

#### - ServiceImpl Class
```java
package sunghs.boot.web.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sunghs.boot.web.mapper.MainMapper;
import sunghs.boot.web.service.MainService;

public @Service class MainServiceImpl implements MainService {

	@Autowired 
	private MainMapper mainMapper;
	
	@Override
	public List<Map<String, Object>> getPacketStat(String packetStatDate) {
		// TODO Auto-generated method stub
		return mainMapper.selectPacketStat(packetStatDate);
	}
	
}
```

### 비즈니스 로직을 수행해 줄 Controller
```java
package sunghs.boot.web.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;
import sunghs.boot.web.service.MainService;

public @Controller @Slf4j class MainController {

	@Autowired
	private MainService mainService;
	
	@RequestMapping(value="/selectPacket")
	public String selectPacket() {
		List<Map<String, Object>> list = mainService.getPacketStat("2019071817");
		for(Map<String, Object> row : list) {
			String pf = row.get("packetFrom").toString();
			String pt = row.get("packetTo").toString();
			String stc = row.get("packetStreamCount").toString();
			log.info("PACKET_FROM : {}, PACKET_TO : {}, PACKET_STREAM_COUNT : {}", pf, pt, stc);
		}
		return "test2";
	}
}

```

### RequestMapping을 받아 줄 test2.html 작성 
```html
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>TEST PAGE</title>
</head>
<body>
	<h1>TEST2 PAGE !</h1>
	<h1>SUCCESS.</h1>
</body>
</html> 
```