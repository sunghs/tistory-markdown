## [REDIS] Redis 설치 (for Windows 64bit)

### Redis (Remote dictionary Server) 란?
 key-value를 기반으로 하는 Memory DB
비슷한걸로는 Oracle NoSQL DataBase, Memcached 등이 있다.

당연히 memory에 관리하므로 프로세스가 내려간다거나, 컴퓨터가 off 되면 모든 데이터는 날아간다.
redis의 또 다른 특징 중 redis는 메모리에 있는 데이터를 디스크에 통째로 찍어내는 SnapShot 기능이 있다.

---
### Redis 특징

1. KEY-VALUE를 쌍으로 하는 MemoryDB
value의 자료형은 String, Set, Hashes, List, Sorted Set 등의 구조를 가질 수 있다.

![?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FbttRFb%2FbtqA6EnXugJ%2FdMYUKvhdkKU3JWtkP2cVPk%2Fimg.png](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FbttRFb%2FbtqA6EnXugJ%2FdMYUKvhdkKU3JWtkP2cVPk%2Fimg.png)

*출처 : [Data Structures \| Redis Labs](https://redislabs.com/redis-enterprise/data-structures/)*

2. Memory Shapshot 기능
메모리에 있는 데이터를 디스크에 통째로 찍어낼 수 있는 기능
특정시점의 데이터를 찍어내며, 서버 restart 시 해당 스냅샷을 다시 메모리에 올릴 수 있다.

3. Replication 기능
redis를 master-slave 구조로 만들어 master의 내용을 slave에 복제 할 수 있다.
master-slave 는 1-N 구조를 가진다.
MasterRedis는 WriteOnly, SlaveRedis ReadOnly 형태를 만들어 성능을 높일 수 있다.

---

기본적으로 Redis는 Unix 환경만 지원한다.
Windows 에서 사용하려면, 기존 Redis를 Windows 환경에 맞춰 사용 할 수 있도록 바꿔야 하는데, github에 Redis for Windows 를 릴리즈 해 주는 팀이 있다.

Linux/Unix : https://redis.io/download (Stable)
Windows 64Bit : https://github.com/microsoftarchive/redis/releases

\2020. 01. 13 기준으로 redis 공식 stable 버전은 5.0.7, Windows 버전은 3.0.5이다.

여기서 설치는 Windows로 진행한다.

---

#### 설치 폴더 내의 redis-server.exe를 실행하면 redis 서버가 구동된다.
기본적으로 Listen Port는 6379 port이다.

![?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FT1X87%2FbtqA6qcq5WN%2F1hBCVCaoixGQyxkSAxUVn1%2Fimg.png](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FT1X87%2FbtqA6qcq5WN%2F1hBCVCaoixGQyxkSAxUVn1%2Fimg.png)

#### 설치 폴더 내의 redis-cli.exe를 실행하면 Redis Client Command 가 실행된다.

![?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FcZpHg3%2FbtqA5PKuBbL%2FkhwdZiPiIFkXwCZABAcZDk%2Fimg.png](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FcZpHg3%2FbtqA5PKuBbL%2FkhwdZiPiIFkXwCZABAcZDk%2Fimg.png)

#### DATA SET

![?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FbVfOK9%2FbtqA57jTfqo%2FosJwpvDIojSumpwg7AKLVK%2Fimg.png](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FbVfOK9%2FbtqA57jTfqo%2FosJwpvDIojSumpwg7AKLVK%2Fimg.png)


#### DATA GET
![?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2Fl9xoy%2FbtqA6pxQidg%2FrkEHiNmyVADa1Ni6i06q80%2Fimg.png](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2Fl9xoy%2FbtqA6pxQidg%2FrkEHiNmyVADa1Ni6i06q80%2Fimg.png)
---


### Listen Port 등 설정을 바꾸고 싶다면
exe 파일을 클릭해서 실행하면 기본 설정을 물고 올라간다. 그래서 변경된 설정의 설정파일을 argument로 구동시키면 된다.

*예) port를 9999로 변경*

![?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FbwCFyV%2FbtqA8irdyGN%2FFDOdSNADA0Ua2497n3KD00%2Fimg.png](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FbwCFyV%2FbtqA8irdyGN%2FFDOdSNADA0Ua2497n3KD00%2Fimg.png)

#### cmd) redis-server redis.windows.conf

![?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2Fdmbmw2%2FbtqBcxA9eJM%2FCAlKuuEnoAWk0YGc1xqXo1%2Fimg.png](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2Fdmbmw2%2FbtqBcxA9eJM%2FCAlKuuEnoAWk0YGc1xqXo1%2Fimg.png)

redis-cli의 기본값도 6379이므로, redis-cli 파라미터로 port를 넣으면된다.
#### cmd) redis-cli -p 9999

![?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FYnYdN%2FbtqA4zgX0K7%2FrWUUE85ZlzQWvGTUcdPb9K%2Fimg.png](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FYnYdN%2FbtqA4zgX0K7%2FrWUUE85ZlzQWvGTUcdPb9K%2Fimg.png)

#### ps. Linux/Unix 환경에서 redis 설치 하는법

##### 설치
$ wget http://download.redis.io/releases/redis-5.0.7.tar.gz
$ tar xzf redis-5.0.7.tar.gz
$ cd redis-5.0.7
$ make

##### 서버 실행
src/redis-server

##### 클라이언트 실행
$ src/redis-cli

##### DATA SET / GET
redis> set foo bar
OK
redis> get foo
"bar"

