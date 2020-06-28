## Hash 암호화 (MD5, SHA-256)

[GitHub 소스](https://github.com/sunghs/java-utils)

Hash 기반 암호화는 단방향 암호화로, 한번 암호화 되면 복호화 할 수 없다.  
입력 길이가 어떻든 출력 길이가 같아서 비둘기 집 원리에 의해 결과값 충돌은 이론상 피할 수 없어서 그렇다.  
**(충돌 : 입력이 다른데, 암호화 된 출력이 같은 경우)**  

심지어 MD5는 충돌 재현이 되어서 MD5는 거의 안쓰이고 현재는 SHA-256 이상부터 쓰이고 있다.  

MD5 출력값 갯수 16^32개  
SHA-256 출력값 갯수 16^64개

**dependency 추가 없음**

### 코드
```java
/**
 * Hash 단방향 암호화
 *
 * @author https://sunghs.tistory.com
 * @see <a href="https://github.com/sunghs/java-utils">source</a>
 */
public class HashCipher {

    private static final int UNSIGNED_BYTE = 0xff;

    private static final String ENCODING_TYPE = "UTF-8";

    public static String toMd5(final String str) {
        try {
            return encrypt(MessageDigest.getInstance("MD5"), str);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toSha256(final String str) {
        try {
            return encrypt(MessageDigest.getInstance("SHA-256"), str);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String encrypt(MessageDigest digest, String str) throws UnsupportedEncodingException {
        StringBuilder buffer = new StringBuilder();
        final byte[] hash = digest.digest(str.getBytes(ENCODING_TYPE));
        for (byte b : hash) {
            buffer.append(hashToHex(b));
        }
        return buffer.toString();
    }

    private static String hashToHex(byte hash) {
        String hex = Integer.toHexString(UNSIGNED_BYTE & hash);
        return hex.length() == 1 ? ("0" + hex) : hex;
    }
}
```
Hash값을 16진수로 바꿀 때 0xff를 and 연산자로 한번 펼치는걸 볼 수 있는데
이는 맨 앞의 부호값을 사용하지 않으려고 하는 방법이다.

자바 버전에 따른 소스 차이는 없으니 바로 복붙해서 사용 가능

### 테스트
```java
String plain = "this is plain text. 이건 평문 입니다.";

@Test
public void md5Test() {
    String hash = HashCipher.toMd5(plain);
    Assertions.assertEquals("7169734D00087E487FF362FE8D14234F", hash.toUpperCase());
}

@Test
public void sha256Test() {
    String hash = HashCipher.toSha256(plain);
    Assertions.assertEquals("CBEE28CB9D02F3E92E8DA297FA1FFA464BE85799A74E16526DB9DB7591B231EC", hash.toUpperCase());
}
```

결과 값의 대소문자는 가리지 않는다. 
문자열 자체는 다른 값일지는 몰라도, bbB1aaa와 BBb1AaA는 같은 출력이다.