## [JAVA] 바이너리 파일 스트링 변환 (Binary to String)

실행파일인 exe나 특정 프로그램으로 실행하는 docs 등 바이너리 파일을 new String(byte[] b) 등으로 스트링으로 변환한다거나 다시 스트링을 getBytes() 등으로 byte[] 바이너리 배열로 바꾼다거나 할때 데이터 손실이 일어난다.


***
- exe 파일의 byte[] -> new String(byte[] b) -> _**데이터 손실**_ -> getBytes()로 byte[] b -> _**데이터손실**_

***
**정상적인 exe파일로 돌아오지 않음.**

###  

### 해결방법은
2진수로 된 바이너리를 아스키로 정상 형변환 해줘야 한다.

[함수 출처](https://frontjang.info/entry/Java-Byte-%EB%B0%B0%EC%97%B4%EA%B3%BC-%EB%B0%94%EC%9D%B4%EB%84%88%EB%A6%AC%EB%AC%B8%EC%9E%90%EC%97%B4-%EC%83%81%ED%98%B8-%EB%B3%80%ED%99%98%ED%95%98%EA%B8%B0)

```java
/**
 * Binary Byte[] to String
 * 
 * @param b
 * @return
 */
public static String byteArrayToBinaryString(byte[] b) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < b.length; ++i) {
        sb.append(byteToBinaryString(b[i]));
    }
    return sb.toString();
}
 
/**
 * Binary byte to String
 * 
 * @param n
 * @return
 */
public static String byteToBinaryString(byte n) {
    StringBuilder sb = new StringBuilder("00000000");
    for (int bit = 0; bit < 8; bit++) {
        if (((n >> bit) & 1) > 0) {
            sb.setCharAt(7 - bit, '1');
        }
    }
    return sb.toString();
}
 
/**
 * Binary String to byte[]
 * 
 * @param s
 * @return
 */
public static byte[] binaryStringToByteArray(String s) {
    int count = s.length() / 8;
    byte[] b = new byte[count];
    for (int i = 1; i < count; ++i) {
        String t = s.substring((i - 1) * 8, i * 8);
        b[i - 1] = binaryStringToByte(t);
    }
    return b;
}
 
/**
 * Binary String to byte
 * 
 * @param s
 * @return
 */
public static byte binaryStringToByte(String s) {
    byte ret = 0, total = 0;
    for (int i = 0; i < 8; ++i) {
        ret = (s.charAt(7 - i) == '1') ? (byte) (1 << i) : 0;
        total = (byte) (ret | total);
    }
    return total;
}
```
### 사용방법
Binary byte[] -> String 으로 변환 : byteArrayToBinaryString
String -> Binary byte[] 으로 변환 : byteStringToByteArray

### 변환 원리
비트연산자와 시프트연산자를 이용해서 바이너리(이진)을 아스키로 바꾸고, 아스키를 2진법으로 변환하는 과정으로 변환한다.
