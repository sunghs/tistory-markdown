# Enum 멤버 변수로 Enum 객체 찾기

### 예를들어 결과 코드를 정의한 enum이 있다.
```java
public enum ExamEnums {

    SUCCESS("200", "성공"),
    WAITING("300", "대기 중"),
    FAIL("500", "실패"),
    ETC("999", "기타 에러");

    @Getter
    private final String code;

    @Getter
    private final String description;

    ExamEnums(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
```

어느 REST에서 resultCode를 보내주고, 그 코드와 설명을 enum에 정의했을 때 위와 같이 작성 할 수 있다.
근데 문제는 SUCCESS란 멤버를 가지고 code 멤버와 description을 찾을 순 있지만,
code를 가지고 SUCCESS 객체의 description을 가져올 방법이 없다.

그래서 예를 들어 code가 200인 경우 , 200을 가지고 SUCCESS enum 객체를 찾는 방법이 있다.
code값을 key로 하고 enum 이름의 string을 value로 하는 static Map에 미리 올려놓는 방법이다.

static 객체이므로, 앱 구동 시 딱 1회 수행된다.

### 추가 할 Code
```java
private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
    Stream.of(values()).collect(Collectors.toMap(ExamEnums::getCode, ExamEnums::name)));

public static ExamEnums of(final String code) {
    return ExamEnums.valueOf(CODE_MAP.get(code));
}
```

위 코드를 추가하면 CODE_MAP 이라는 객체에는 code를 key, enum명을 value로 하는 UnmodifiableMap이 완성된다.
그리고 아래의 of 메소드를 통해 실제 Enum 객체로 넘겨준다.

Enum의 멤버변수가 여러가지라면 저렇게 Enum을 받아 이것저것 데이터를 꺼내 쓰면 되겠고, 실제로 description만 필요한거라면
최초 CODE_MAP의 Stream으로 Map을 만들 때 ExamEnums::name 대신 ExamEnums::getDescription 함수를 써서 value 부분에 description을 만들면 된다.

## 최종 Enum Code
```java
public enum ExamEnums {

    SUCCESS("200", "성공"),
    WAITING("300", "대기 중"),
    FAIL("500", "실패");

    private static final Map<String, String> CODE_MAP = Collections.unmodifiableMap(
        Stream.of(values()).collect(Collectors.toMap(ExamEnums::getCode, ExamEnums::name)));
    @Getter
    private final String code;

    @Getter
    private final String description;

    ExamEnums(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ExamEnums of(final String code) {
        return ExamEnums.valueOf(CODE_MAP.get(code));
    }
}
```

## 테스트
```java
@Slf4j
public class TestClass {

    public static void main(String[] args) {
        String resultCode = "500";

        ExamEnums examEnums = ExamEnums.of(resultCode);

        log.info(examEnums.getCode());
        log.info(examEnums.getDescription());
    }
}
```

## 결과
```
[main] INFO sunghs.tistory.markdown.enumtest.TestClass - 500
[main] INFO sunghs.tistory.markdown.enumtest.TestClass - 실패
```