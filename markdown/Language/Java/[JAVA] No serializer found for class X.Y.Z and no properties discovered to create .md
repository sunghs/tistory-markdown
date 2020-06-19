## No serializer found for class X.Y.Z and no properties discovered to create BeanSerializer

ObjectMapper에 의한 객체를 문자열로 바꿀 때, 유효한 접근자를 찾을 수 없거나 결정할 수 없을 때 발생하는 에러이다. 또는 직렬화 할 어노테이션이 없는 경우에도 발생 할 수 있다.

### 기존 ObjectMapper 설정

```java
public static Optional<String> mapToString(Map<?, ?> map) {
    try {
        return Optional.of(new ObjectMapper().writeValueAsString(map));
    }
    catch(Exception e) {
        log.error("Convert Map to Json Error", e);
        e.printStackTrace();
        return Optional.ofNullable(null);
    }
}
```

위와 같이 ObjectMapper에 아무 설정 없이 바로 사용 하는 코드가 있을 때

```java
public void mapToStringTest() {
    Map<String, Object> map = new HashMap<>();
    map.put("k1", new Object());
    map.put("k2", "literal string");
    map.put("k3", new String("new string"));
    map.put("k4", 67890);
    map.put("k6", true);

    Optional<String> s = CommonUtil.mapToString(map);

    System.out.println(s.get());
}
```

를 실행하는 경우

    12:14:43.291 [main] ERROR sunghs.sniff.util.CommonUtil - Convert Map to Json Error
    com.fasterxml.jackson.databind.exc.InvalidDefinitionException: No serializer found for class java.lang.Object and no properties discovered to create BeanSerializer (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS) (through reference chain: java.util.HashMap["k1"])
    	at com.fasterxml.jackson.databind.exc.InvalidDefinitionException.from(InvalidDefinitionException.java:77)
    	at com.fasterxml.jackson.databind.SerializerProvider.reportBadDefinition(SerializerProvider.java:1191)
    	at com.fasterxml.jackson.databind.DatabindContext.reportBadDefinition(DatabindContext.java:404)
    	at com.fasterxml.jackson.databind.ser.impl.UnknownSerializer.failForEmpty(UnknownSerializer.java:71)
    	at com.fasterxml.jackson.databind.ser.impl.UnknownSerializer.serialize(UnknownSerializer.java:33)
    	at com.fasterxml.jackson.databind.ser.std.MapSerializer.serializeFields(MapSerializer.java:722)
    	at com.fasterxml.jackson.databind.ser.std.MapSerializer.serialize(MapSerializer.java:643)
    	at com.fasterxml.jackson.databind.ser.std.MapSerializer.serialize(MapSerializer.java:33)
    	at com.fasterxml.jackson.databind.ser.DefaultSerializerProvider._serialize(DefaultSerializerProvider.java:480)
    	at com.fasterxml.jackson.databind.ser.DefaultSerializerProvider.serializeValue(DefaultSerializerProvider.java:319)
    	at com.fasterxml.jackson.databind.ObjectMapper._configAndWriteValue(ObjectMapper.java:4094)
    	at com.fasterxml.jackson.databind.ObjectMapper.writeValueAsString(ObjectMapper.java:3404)
    	at sunghs.sniff.util.CommonUtil.mapToString(CommonUtil.java:28)

와 같은 에러가 발생한다.

이 때 "k1"을 키로 넣은 value의 new Object() 에 직렬화 할 내용을 못찾아서 발생하는 에러이므로, 이 속성에 대한 설정을 비활성화 하면 직렬화 내용이 없는경우 그냥 건너 뛸 수 있다.

### ObjectMapper를 Instance 하고 설정메소드를 넣어준다.

```java
public static Optional<String> mapToString(Map<?, ?> map) {
    try {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return Optional.of(objectMapper.writeValueAsString(map));
    }
    catch(Exception e) {
        log.error("Convert Map to Json Error", e);
        e.printStackTrace();
        return Optional.ofNullable(null);
    }
}
```

#### SerializationFeature.FAIL_ON_EMPTY_BEANS의 설명

* * *

유형에 대한 접근자를 찾을 수 없을 때 발생하는 작업을 결정하는 기능입니다 (직렬화 할 것을 나타내는 주석이 없습니다). 사용 가능한 경우 (기본값), 직렬화 할 수없는 유형으로 표시하기 위해 예외가 발생합니다. 비활성화하면 빈 객체로 직렬화됩니다. 즉, 속성이 없습니다.

이 기능이 비어있는 유형은 @JsonSerialize와 같이 인식 된 주석이없는 "빈"Bean에만 영향을 미칩니다. 주석이있는 주석은 예외가 발생하지 않습니다.

기능은 기본적으로 활성화되어 있습니다.

* * *

#### 설정 이후 mapToString 결과 (sysout)

* * *

{"k1":{},"k2":"literal string","k3":"new string","k4":67890,"k6":true}

* * *

[출처]<https://stackoverflow.com/questions/15261456/how-do-i-disable-fail-on-empty-beans-in-jackson>
