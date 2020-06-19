## [Windows] Port 번호로 PID 찾기, PID 구동시간 확인


### 포트번호로 pid 찾기

```xml
netstat -ano | findstr $PORT
```

옵션에 포트를(a), 숫자형식(n), 그리고 pid를 함께(o) 표시하는 옵션으로 검색.
따라서 실제 ano 옵션으로는 프로토콜, 로컬주소(ip:port), 외부주소, 상태(ESTABLISHED, LISTENING, TIME_WAIT), PID 전부 검색이 가능하다.

![?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FMz6UQ%2FbtqCNaxSYDd%2FCJyLDE7qVR2MeKWK5Dn3ck%2Fimg.png](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FMz6UQ%2FbtqCNaxSYDd%2FCJyLDE7qVR2MeKWK5Dn3ck%2Fimg.png)

### 해당 포트번호로 구동시간 확인

```xml
wmic path win32_process get processid, creationdate
```