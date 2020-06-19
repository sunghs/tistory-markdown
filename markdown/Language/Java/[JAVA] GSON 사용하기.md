### GSON 사용하기

**Gson은 Json을 좀 더 편하게 사용할 수 있도록 Google에서 만든 Google Json 라이브러리
Object Class와 Json 간의 직렬/역직렬을 편하게 사용 가능하도록 도와줌**
####  

JsonSimple, Gson, Jackson 등의 json 라이브러리가 많은데, 대규모 용량이 아닌 상황에서는 gson이 좋은 성능을 보여줌.

Maven 프로젝트인 경우 pom에 추가하고, 아닌경우 URL 들어가서 jar build path에 추가
```xml
<!-- https://mvnrepository.com/artifact/com.google.code.gson/gson -->
<dependency>
	<groupId>com.google.code.gson</groupId>
	<artifactId>gson</artifactId>
</dependency>
```

#### TEST용 Object Class
```java
package sunghs.boot.gson;

import java.util.UUID;

import lombok.Data;

public @Data class Models {

	private String data1 = null;
	
	private String data2 = null;
	
	private long data3 = 0;
	
	private boolean data4 = false;
	
	public Models() {
		this.data1 = UUID.randomUUID().toString();
		this.data2 = UUID.randomUUID().toString();
		this.data3 = System.currentTimeMillis();
		this.data4 = true;
		System.out.println("CALL CONSTRUCT");
	}
	
	public Models(String s) {
		this.data3 = System.currentTimeMillis();
		this.data4 = false;
		System.out.println("CALL NULL CONSTRUCT");
	}
}

```
기본 생성자 호출 시 data1 ~ data4 의 멤버변수에 값을 모두 세팅하고
String parameter가 들어가는 생성자 호출 시 data1, data2는 null로 세팅되게 해 놓았다.

***
#### Object to Json

```java
//OBJECT INSTANCE
Models m = new Models();

//OBJECT TO JSON
String json = new Gson().toJson(m);
System.out.println(json);
```

CALL CONSTRUCT
{"data1":"2d1aa9a8-59bd-4966-b48f-bc4cc84f9490","data2":"96de6773-447d-47e5-a989-48581a3fa6ae","data3":1563500502052,"data4":true}


#### Json to Object
```java
//JSON TO OBJECT
m = new Gson().fromJson(json, Models.class);
System.out.println(m.toString());
```

CALL CONSTRUCT
Models(data1=2d1aa9a8-59bd-4966-b48f-bc4cc84f9490, data2=96de6773-447d-47e5-a989-48581a3fa6ae, data3=1563500502052, data4=true)

####  
데이터 변경 없이 정상 변환
***

####  

#### **Object Instance 시 변수에 null이 포함되어 있다면 문제가 될 수 있음**

```java
//DATA1, DATA2 IS NULL
m = new Models("");

json = new Gson().toJson(m);
System.out.println(json);
```
CALL NULL CONSTRUCT
{"data3":1563500502219,"data4":false}

####  
toJson 시 data1, data2는 빠진걸 볼 수 있는데

```java
//DATA1, DATA2 IS NOT NULL
m = new Gson().fromJson(json, Models.class);
System.out.println(m.toString());
```
CALL CONSTRUCT
Models(data1=42ef79e2-2248-41f5-abf6-9b794bbcd585, data2=8c2ed64d-24a2-465e-a184-8b2e23879921, data3=1563500502219, data4=false)

####  
fromJson 메소드에서 기본 생성자를 호출해서 data1, data2의 데이터가 생겨버렸음
이런경우 원래의 Models 객체와 데이터가 달라지는 현상이 발생함.
***

#### **그래서 null이 포함 될 가능성이 있는 Object는 null도 직렬화 해야한다.**
```java
m = new Models("");
// null인 멤버 또한 Serialize
json = new GsonBuilder().serializeNulls().create().toJson(m);
System.out.println(json);
```
CALL NULL CONSTRUCT
{"data1":null,"data2":null,"data3":1563501340346,"data4":false}

####  
null 멤버값 또한 직렬화해서 toJson으로 변했다.

```java
m = new Gson().fromJson(json, Models.class);
System.out.println(m.toString());
```
Json to Object는 동일하게 처리한다.
####  

**CALL CONSTRUCT
Models(data1=null, data2=null, data3=1563501340346, data4=false)**

####  
기본 생성자 호출은 동일하지만, data1, data2에 null을 대입하게 된다.
