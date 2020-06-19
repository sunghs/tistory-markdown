## [JAVA] 두 날짜 사이 월 구하기

```java
public static List<String> getMonthList(String startDate, String endDate) {
         
    List<String> result = new ArrayList<String>();
     
    int sy = Integer.parseInt(startDate.substring(0, 4));
    int sm = Integer.parseInt(startDate.substring(4, 6));
    int ey = Integer.parseInt(endDate.substring(0, 4));
    int em = Integer.parseInt(endDate.substring(4, 6));
     
    int period = (ey - sy) * 12 + (em - sm);
     
    if(period == 0) 
        result.add(startDate.substring(0, 6));
    else {
        result.add(startDate.substring(0, 6));
        for(int i = 0; i < period; i ++) {
            if(sm == 12) {
                sy++; 
                sm = 1;
            }
            else {
                sm++;
            }
            if(sm < 10) 
              result.add(String.valueOf(sy) + "0" + String.valueOf(sm));
            else 
              result.add(String.valueOf(sy) + String.valueOf(sm));
        }
    }
    return result;
}
```

###  

#### 만약 파라미터가 20190105 , 20190713 일 때,
201901, 201902, 201903, 201904, 201905, 201906, 201907 가 담긴 LIST로 반환
나중에 월단위로 생성되는 테이블에서 조회할때 조회할 테이블 LIST를 만들때 쓰기 좋을 것 같다.

