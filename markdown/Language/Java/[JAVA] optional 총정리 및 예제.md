---
title: "Java Optional 총정리 및 예제"
tags: "Java"
---
# Java Optional 총정리 및 예제

Java의 Optional Class는 NullPointerException에 유연하게 대처 할 수 있도록 JDK 8부터 지원하기 시작했다.  
if-else 지옥에 빠지지 않도록 하며, 객체의 null check를 메소드 호출로써 대체할 수 있다.

## 선언과 instance

Optional의 원형은 Optional&lt;T> 형태로 제네릭 T를 감싸고 있다. 이 T의 자료형이 null check의 타겟이 된다.

```java
public static <T> Optional<T> of(T value) {
    return new Optional<>(value);
}

public static <T> Optional<T> ofNullable(T value) {
    return value == null ? empty() : of(value);
}
```

위 두 메소드로 instance 한다.  
딱 봐도 알겠지만 of는 null을 허용하지 않고, ofNullable은 null일 경우 비어있는 상수를 가져와 넣는다.

```java
String str1 = "hi";
Optional<String> optional1 = Optional.of(str);


String str2 = null;
Optional<String> optional2 = Optional.ofNullable(str2); //NullPointerException 발생
Optional<String> optional3 = Optional.of(str2); //가능
```

반드시 null이 아닌 객체는 of로, null일 수도 있는 객체는 ofNullable로 받는다.

## 주요 메소드 목록

Optional의 메소드를 전부 나열 한 것은 아니고, 자주 사용되고 Optional을 쓴다면 반드시 알아야 하는 메소드를 나열한다. 그리고 Optional은 JDK 8부터 지원하기 시작했으나 JDK 버전이 올라 갈 수록 함수형 인터페이스의 추가/변경 등으로 인해 JDK 11까지 변경점이 있다. 아래 쓰는 내용은 JDK 11을 기준으로 하고 있고, JDK 8에는 없는 문법/메소드/클래스 등이 있을 수 있다.

### 1. static Optional&lt;T> empty()

Optional이 감싼 객체를 제거한다.

```java
Optional<String> opt = Optional.empty();
```

### 2. boolean isPresent()

Optional이 감싸고 있는 객체가 null이 아니면 true, null이면 false를 반환

### 3. boolean isEmpty()

isPresent()와 반대로 null인 경우 true를 반환

### 4. T get()

감싼 객체를 반환한다.

```java
String result = optional1.get();
```

### 5. void ifPresent(Consumer \\&lt;? super T> action)

비어있지 않다면 할 action을 정의한다. Consumer는 함수형 인터페이스이므로 람다로 구현한다.  
isPresent()와 헷갈리지 않도록 하자.

```java
@Test
public void optional() {
    String str = "hi";
    Optional<String> optional = Optional.of(str);

    //Logger가 hi를 찍는다.
    optional.ifPresent((act) -> log.info(act));
}
```

### 6. void ifPresentOrElse(Consumer&lt;? super T> action, Runnable emptyAction)

ifPresent와 비슷하지만 Runnable emptyAction이 추가 되었다.  
객체가 들어있다면 action을, 없다면 emptyAction을 실행한다.  
위의 간단한 메소드 말고 Optional은 이 메소드같이 null 일 때 핸들링 처리를 위한 위해 나온게 아닌가 싶다.  

```java
@Test
public void optional() {
    String str = null;
    Optional<String> optional = Optional.ofNullable(str);

    //str이 null인지 아닌지에 따라 달라진다.
    optional.ifPresentOrElse((act) -> {
        log.info("들어있으면 이걸 실행한다.");
    }, () -> {
        log.info("안 들어있으면 이걸 실행한다."); //이게 출력됨
    });
}
```

### 7. &lt;X extends Throwable> T orElseThrow(Supplier&lt;? extends X> exceptionSupplier) throws X

객체가 있으면 반환, 없다면 Throwable을 구현한 구현체의 Supplier를 반환한다.   Supplier도 함수형 인터페이스이므로 람다로 구현해야 한다.

```java
optional.orElseThrow(() -> new RuntimeException("비어있어요..")); //custom message
optional.orElseThrow(RuntimeException::new); //default
```

### 8. T orElseThrow()

위와 같지만 exception을 구현하지 않아도 된다. 내부 구현방식을 보면 NoSuchElementException을 던진다.

### 9. Optional&lt;T> filter(Predicate&lt;? super T> predicate)

Predicate 객체를 통과하지 못하면 빈 값을 return 한다.  
통과에 대한 로직 또한 위와 같이 따로 구현한다.

### 10. &lt;U> Optional&lt;U> map(Function&lt;? super T, ? extends U> mapper)

객체가 null이 아닐경우, mapper를 통해 변환 된 Optional 객체를 반환한다. 
mapper를 통과하지 못한 경우 empty()를 반환한다.

```java
Optional<Integer> i = Optional.of(5);
Optional<String> s = i.map(String::valueOf);

log.info(s.get()); //5가 출력된다.
```

* * *

위에서 봤듯이 대부분이 큰 로직만 제공하고, 내부는 개발자가 직접 구현해서 쓸 수 있도록 해놓았다.  
간단한 로직에서는 가독성면에서 오히려 if-else가 좋을 수 있으나 연속 if-if-if-else-else-else과 같은 조건문 지옥이 있는 상황에서는 써볼 만 할 것 같다.

## 빠르게 예제보기 (긁어다 쓰는 용도)

### String 문자열 체크 Optional 변경

```java
//기존
if(str == null || str.equals("")) {
    str = "alternative";
}

//대체
str = Optional.ofNullable(str).filter(Predicate.not(String::isBlank)).orElseGet(() -> "alternative");
```

apache-common의 StringUtils도 대안이 될 수 있다.  
Optional이 정답은 아니다.

```java
//implementation apache-common
if(StringUtils.isEmpty(str)) {
	//TODO
}
```

### 필수 파라미터 Exception을 내야 하는 상황이라면

```java
//기존
if(str == null || str.equals("")) {
    throw new IllegalArgumentException();
}

//대체
Optional.ofNullable(str).filter(Predicate.not(String::isBlank)).orElseThrow(IllegalArgumentException::new);
```
