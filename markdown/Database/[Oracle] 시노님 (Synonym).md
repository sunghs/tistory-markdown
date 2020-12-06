## [Oracle] 시노님 (Synonym)

A 계정으로 만들어진 TABLE이 있을 때 이 테이블을 다른 계정에서 사용하고 싶다면 "소유자.테이블명" 으로 쿼리에 줘야 함.

USER1의 TABLE1 라는 테이블이 있는데, USER2 가 해당 테이블을 조회 한다면 
***
SELECT * FROM USER1.TABLE1
***
가 된다.

이런 소유자명을 매번 붙여 작성하는게 번거롭고 힘들다면 해당 문자열 자체를 ALIAS를 줘 바꿔버리는 방법 = **Synonym**

위와 같은 경우 USER1.TABLE1 을 TABLE1로 시노님을 주면 SELECT * FROM TABLE1로 사용 가능.
시노님을 주려면 일단 해당 테이블에 대해 접근 권한이 있어야 하므로 권한부여 전처리가 보통 먼저 들어간다.

### USER1 의 TABLE1 에 대해 USER2 한테 권한을 부여

### 시노님 생성
시노님은 계정 관점에서 SELECT 해야 되므로, USER1로 로그인해서 시노님을 조회 하는게 아니라 USER2로 로그인해서 시노님을 조회해야 한다. 
USER1로 로그인해서 시노님을 조회하면 USER1이 사용하는 시노님만 조회된다. 
로그인 한 계정에 따라 WHERE USER_NAME = '로그인계정명' 정도가 자연스럽게 붙는다고 생각할 수 있다.


#### 1. USER2로 로그인 한 후 SYNONYM 조회

___SELECT * FROM ALL_SYNONYMS WHERE SYNONYM_NAME = '?'___
SYNONYM_NAME 값은 이미 그 이름으로 ALIAS를 준게 있는지 확인하는 것이므로 USER1.TABLE1을 AAA1로 치환했다면 AAA1이 있는지 확인해봐야 한다.

#### 2. 없다면 SYNONYM 생성

USER1.TABLE1 을 AAA1로 치환하는 경우
CREATE SYNONYM AAA1 FOR USER1.TABLE1
앞으로 
___SELECT * FROM AAA1___ 과 ___SELECT * FROM USER1.TABLE1___ 은 같음.


#### 3. USER2 계정이 CREATE SYNONYM 쿼리에 대해 권한이 불충분하다고 나오는 경우에는 DBA 계정으로 USER2에 시노님 생성 권한을 줘야함.

___GRANT CREATE SYNONYM TO USER2___
이후 시노님을 생성하면 된다.