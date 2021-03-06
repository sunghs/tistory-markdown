## [ORACLE] TABLE, INDEX CREATE DDL 추출

각종 툴을 쓰면 보통 TABLE, INDEX DDL SCRIPT 추출 기능이 있는데, 오라클 쿼리로도 가져올 수 있다.


### SUNGHS.USER_INFO 테이블, 인덱스 추출
특정 테이블명과 인덱스명을 알 때 이다.
TABLE 명 : USER_INFO
IDX 명 : IDX_USER_INFO
```sql
SELECT DBMS_METADATA.GET_DDL('TABLE', 'USER_INFO', 'SUNGHS') FROM DUAL

SELECT DBMS_METADATA.GET_DDL('INDEX', 'IDX_USER_INFO', 'SUNGHS') FROM DUAL
```

### 추출에 조건을 걸 수 있다.
```sql
--- SUNGHS.A*로 시작하는 테이블만
SELECT DBMS_METADATA.GET_DDL('TABLE', TABLE_NAME, 'SUNGHS') FROM DBA_TABLES WHERE TABLE_NAME LIKE 'A%'

--- IDX_A*로 시작하는 인덱스만
SELECT DBMS_METADATA.GET_DDL('INDEX', INDEX_NAME, 'SUNGHS') FROM DBA_INDEXES WHERE TABLE_NAME LIKE 'IDX_A%'
```

### 특정 스키마의 TABLE, INDEX 전부 추출
```sql
SELECT DBMS_METADATA.GET_DDL('TABLE', TABLE_NAME) FROM USER_TABLES

SELECT DBMS_METADATA.GET_DDL('INDEX', INDEX_NAME) FROM USER_INDEXES
```

