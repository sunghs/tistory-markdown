## SEED128 양방향 암호화

[Github](https://github.com/sunghs/java-utils)
AES-128과 같이 대칭키를 가진 양방향 암호화이다. 암호화 및 복호화가 가능하다.
유저키가 16자리를 기반으로 암/복호화를 진행한다.

국내 KISA에서 개발되었으며 JAVA 기본 내장 알고리즘이 아니므로 SEED 블록 알고리즘 클래스 파일이 필요하다.  
너무 길어서 gitHub에 올려놓았다.  
https://github.com/sunghs/java-utils/blob/master/src/main/java/sunghs/java/utils/cipher/support/KisaSeedCbc.java

### 소스
```java
/**
 * SEED-128 암호화
 *
 * @author https://sunghs.tistory.com
 * @see <a href="https://github.com/sunghs/java-utils">source</a>
 */
public class Seed128 {

    private static final Charset ENCODING_TYPE = StandardCharsets.UTF_8;

    private final byte[] key;

    private final byte INIT_VECTOR[] = {
        (byte) 0x026, (byte) 0x08d, (byte) 0x066, (byte) 0x0a7, (byte) 0x035, (byte) 0x0a8,
        (byte) 0x01a, (byte) 0x081, (byte) 0x06f, (byte) 0x0ba, (byte) 0x0d9, (byte) 0x0fa,
        (byte) 0x036, (byte) 0x016, (byte) 0x025, (byte) 0x001
    };

    public Seed128(final String key) {
        validation(key);
        this.key = key.getBytes(ENCODING_TYPE);
    }

    public String encrypt(final String str) {
        byte[] strBytes = str.getBytes(ENCODING_TYPE);
        byte[] encrypted = KisaSeedCbc.SEED_CBC_Encrypt(this.key, this.INIT_VECTOR, strBytes, 0, strBytes.length);
        return new String(Base64.getEncoder().encode(encrypted), ENCODING_TYPE);
    }

    public String decrypt(final String str) {
        byte[] decoded = Base64.getDecoder().decode(str.getBytes(ENCODING_TYPE));
        byte[] decrypted = KisaSeedCbc.SEED_CBC_Decrypt(this.key, this.INIT_VECTOR, decoded, 0, decoded.length);
        return new String(decrypted, ENCODING_TYPE);
    }

    private void validation(final String key) {
        Optional.ofNullable(key)
            .filter(Predicate.not(String::isBlank))
            .filter(Predicate.not(s -> s.length() != 16))
            .orElseThrow(IllegalArgumentException::new);
    }
}
```

### 테스트
```java
@Test
public void test() {
    Seed128 seed128 = new Seed128("password12345678");
    String plain = "this is plain text. 가나다라마바사. 123456";
    String enc = seed128.encrypt(plain);
    String dec = seed128.decrypt(enc);

    log.info(enc);
    log.info(dec);

    Assertions.assertEquals(plain, dec);
}

@Test
public void passwordOverLength() {
    // 유저 키가 16자리 초과 인 경우 에러
    Seed128 s1 = new Seed128("password1234567890qwertyui");
}

@Test
public void passwordUnderLength() {
    // 유저 키가 16자리 미만 인 경우 에러
    Seed128 s2 = new Seed128("password");
}
```

### 로그
#### 키가 16자리가 아닌경우
java.lang.IllegalArgumentException
	at java.base/java.util.Optional.orElseThrow(Optional.java:408)
	at sunghs.java.utils.cipher.Seed128.validation(Seed128.java:49)
	at sunghs.java.utils.cipher.Seed128.<init>(Seed128.java:29)
    ...

#### 암복호화 테스트
17:56:33.976 [main] INFO sunghs.java.utils.cipher.Seed128Test - MaVVztUHePNkQMWccvGHLW8oaYxfNRx0PbJ0uRB5bZdGMzzlRPSQuQCRdm6IuHcjlNwrlYH8xVMKYSiAk7GVoQ==
17:56:33.978 [main] INFO sunghs.java.utils.cipher.Seed128Test - this is plain text. 가나다라마바사. 123456
