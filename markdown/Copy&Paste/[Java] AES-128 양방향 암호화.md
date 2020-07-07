## AES128 양방향 암호화

[Github](https://github.com/sunghs/java-utils)  
Hash 암호화와는 다르게 대칭키를 가지고 암호화와 복호화를 진행한다.

AES 뒤에 붙는 128이나 192, 256은 대칭키의 bit수를 나타내는 것으로
AES-128의 경우 128bit의 대칭키를 쓰는 암호화 알고리즘이다.
128bit는 16byte이므로, 키의 String length가 16자리이다.
(192bit = 24자리, 256bit = 32자리)

키의 길이에 따라서 암호화 라운드의 수만 다르므로 AES-128과 AES-192, AES-256은 구현되는 소스는 같다.
그러나 Java에서는 AES키 길이에 제한을 두기때문에 현재 공식적으로 AES-128밖에 지원하지 않는다.
AES-128 이상 사용하려면 JRE의 정책 라이브러리를 갈아 끼우는 방법이 있다.

### 소스
```java
/**
 * AES-128 암호화
 *
 * @author https://sunghs.tistory.com
 * @see <a href="https://github.com/sunghs/java-utils">source</a>
 */
public class Aes128 {

    private static final Charset ENCODING_TYPE = StandardCharsets.UTF_8;

    private static final String INSTANCE_TYPE = "AES/CBC/PKCS5Padding";

    private SecretKeySpec secretKeySpec;

    private Cipher cipher;

    private IvParameterSpec ivParameterSpec;

    public Aes128(final String key) {
        validation(key);
        try {
            byte[] keyBytes = key.getBytes(ENCODING_TYPE);
            secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            cipher = Cipher.getInstance(INSTANCE_TYPE);
            ivParameterSpec = new IvParameterSpec(keyBytes);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String encrypt(final String str) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(str.getBytes(ENCODING_TYPE));
        return new String(Base64.getEncoder().encode(encrypted), ENCODING_TYPE);
    }

    public String decrypt(final String str) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decoded = Base64.getDecoder().decode(str.getBytes(ENCODING_TYPE));
        return new String(cipher.doFinal(decoded), ENCODING_TYPE);
    }

    private void validation(final String key) {
        Optional.ofNullable(key)
                .filter(Predicate.not(String::isBlank))
                .filter(Predicate.not(s -> s.length() != 16))
                .orElseThrow(IllegalArgumentException::new);
    }
}
```

생성자에서 키 길이를 검사하며, 16자리가 아닌 경우 Exception을 발생시키도록 해 놓았다.
Optional 클래스를 사용하므로 JDK8 이상에서 사용 가능하고, 그 이전 버전이면 validation 함수를 고쳐야 한다.

### 테스트
```java
@Test
public void test() throws Exception {
    Aes128 aes128 = new Aes128("password12345678");
    String enc = aes128.encrypt("this is plain text");
    String dec = aes128.decrypt(enc);
    log.info(enc);
    log.info(dec);
}
```

### 로그
22:15:46.911 [main] INFO sunghs.java.utils.cipher.Aes128Test - /SN33DYI249vWjlC7z7X5jRdFbi94vraMlqEpv1FCHs=
22:15:46.913 [main] INFO sunghs.java.utils.cipher.Aes128Test - this is plain text