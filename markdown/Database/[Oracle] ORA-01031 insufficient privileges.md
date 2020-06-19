## [ORACLE] ORA-01031 : insufficient privileges

DBA 계정 접속을 위해
$ sqlplus /as sysdba 접속 시 ORA-01031 : insufficient privileges 가 뜬다면
sys 계정의 비밀번호를 같이 입력해야 한다.
sys 계정의 비밀번호가 1234라면

---

**$ sqlplus /nolog
SQL > connect sys/1234 as sysdba**

---

위와 같이 비밀번호 없이 sys 계정에 접근하는 것을 막을 수 있음.