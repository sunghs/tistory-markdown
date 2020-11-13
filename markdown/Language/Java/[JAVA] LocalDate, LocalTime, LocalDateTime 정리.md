# LocalDate, LocalTime, LocalDateTime 정리
JodaTime이 Java의 기본으로 통합되며 생긴 날짜/시간 Object, Java8 부터 사용 가능하다.

#### LocalDate : 년,월,일 정보
#### LocalTime : 시,분,(초),(나노초) 정보
#### LocalDateTime : 년,월,일,시,분,(초),(나노초) 정보
초와 나노초는 항상 생략 가능하다.
 
### 현재시간 구하기
```java
LocalDateTime localDateTime = LocalDateTime.now();

LocalDate localDate = LocalDate.now();

LocalTime localTime = LocalTime.now();
```

### 3종세트 toString
LocalDate : yyyy-MM-dd  
LocalTime : HH:mm:ss  
LocalDateTime : yyyy-MM-ddTHH:mm:ss  
```java
log.info("{}, {}, {}", localDateTime.toString(), localDate.toString(), localTime.toString());
```

```
2020-10-10T20:51:32.304415, 

2020-10-10, 

20:51:32.304843
```

### 특정 날짜 인스턴스
```java
// 년 월 일 시 분 초(생략가능) 나노초(생략가능)
// 2020-01-20T15:30:26:010
localDateTime = LocalDateTime.of(2020, 1, 20, 15, 30, 26, 10);
// 년 월 일
// 2020-12-31
localDate = LocalDate.of(2020, 12, 31);
// 시 분 초(생략가능) 나노초(생략가능)
// 09:36:00:000
localTime = LocalTime.of(9, 36);
```

### LocalDate + LocalTime -> LocalDateTime
```java
LocalDateTime newLocalDateTime = LocalDateTime.of(localDate, localTime);
```

### LocalDateTime -> LocalDate & LocalTime
```java
LocalDate newLocalDate = newLocalDateTime.toLocalDate();
LocalTime newLocalTime = newLocalDateTime.toLocalTime();
```

### 날짜/시간 더하기 빼기
```java
// 일 더하기
newLocalDate.plusDays(1);
// 월 더하기
newLocalDate.plusMonths(1);
// 주 더하기
newLocalDate.plusWeeks(1);
// 연 더하기
newLocalDate.plusYears(1);

// 일 빼기
newLocalDate.minusDays(1);
// 월 빼기
newLocalDate.minusMonths(1);
// 주 빼기
newLocalDate.minusWeeks(1);
// 연 빼기
newLocalDate.minusYears(1);

newLocalTime.plusHours(1);
newLocalTime.plusMinutes(1);
newLocalTime.plusSeconds(1);
newLocalTime.plusNanos(1);
newLocalTime.minusHours(1);
newLocalTime.minusMinutes(1);
newLocalTime.minusSeconds(1);
newLocalTime.minusNanos(1);
```
LocalDateTime 에도 위 날짜/시간 연산 메소드가 다 있다.

### 두 시간 비교
```java
// 해당 연도 시작 생성
// 2020-01-01T00:00:00
LocalDateTime firstDay = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
// 해당 연도 종료 생성
// 2020-12-31T23:59:59
LocalDateTime lastDay = LocalDateTime.of(2020, 12, 31, 23, 59, 59);

// firstDay 가 lastDay 보다 과거인지
boolean thisIsTrue = firstDay.isBefore(lastDay);
// firstDay 가 lastDay 보다 미래인지
boolean thisIsFalse = firstDay.isAfter(lastDay);
// firstDay 랑 lastDay 가 같은지
boolean thisIsFalse2 = firstDay.equals(lastDay);
```
LocalDate 와 LocalTime 도 동일하게 가지고 있음.

### 두 날짜의 차이 값 가져오기
return 값은 long이다.
```java
// 연도차이 = 0
log.info("{}", ChronoUnit.YEARS.between(firstDay, lastDay));
// 달 차이 = 11
log.info("{}", ChronoUnit.MONTHS.between(firstDay, lastDay));
// 일 차이 = 365...
log.info("{}", ChronoUnit.DAYS.between(firstDay, lastDay));
// 시간 차이
log.info("{}", ChronoUnit.HOURS.between(firstDay, lastDay));
// 분 차이
log.info("{}", ChronoUnit.MINUTES.between(firstDay, lastDay));
// 초 차이
log.info("{}", ChronoUnit.SECONDS.between(firstDay, lastDay));
```

LocalDate 와 LocalTime 도 전부 비교 가능하나 없는 값을 비교하면  
java.time.temporal.UnsupportedTemporalTypeException : Unsupported unit: Seconds 발생
```java
// LocalDate는 초가 없기때문에 Exception 발생
log.info("{}", ChronoUnit.SECONDS.between(newLocalDate, newLocalDate));
```

### 출력 포맷 바꾸기
```java
// yyyy-MM-ddTHH:mm:ss 를 yyyy...MM...dd...HH...mm...ss 로 바꿔 출력하기
DateTimeFormatter newPattern = DateTimeFormatter.ofPattern("yyyy...MM...dd...HH...mm...ss");
log.info(newLocalDateTime.format(newPattern));
```
```
19:10:21.942 [main] INFO sunghs.java.utils.time.JavaDateUtils - 2020...12...31...09...36...00
```

### java.util.Date <-> LocalDateTime
```java
// Date -> LocalDateTime
Date date = new Date();
LocalDateTime dateToLdt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());

// LocalDateTime -> Date
LocalDateTime ldt = LocalDateTime.now();
Date ldtToDate = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
```