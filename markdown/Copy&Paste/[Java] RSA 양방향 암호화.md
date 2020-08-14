## RSA 양방향 암호화

[Github](https://github.com/sunghs/java-utils)  

AES/SEED 암호화는 하나의 키를 가지고 암/복호화를 같이하는 대칭키(=암호화/복호화 키가 같음) 암호화 방식이라면
RSA는 암호화 키와 복호화 키가 다른 비대칭키 암호화 이다.

### 키를 만드는 과정
```
1. 두 소수 p , q를 준비한다.
2. p - 1, q - 1과 각각 서로소인 정수 e를 준비한다.
3. ed를 (p - 1)(q - 1)으로 나눈 나머지가 1이 되도록 하는 d를 찾는다.
4. N = pq를 계산한 후, N와 e를 공개한다. 이들이 바로 공개키이다. 한편 d는 숨겨두는데, 이 수가 바로 개인키이다.
5. 이제 p, q, (p - 1)(q - 1)는 필요 없거니와 있어 봐야 보안에 오히려 문제를 일으킬 수 있으니, 파기한다.
```
[나무위키 RSA 설명](https://namu.wiki/w/RSA%20%EC%95%94%ED%98%B8%ED%99%94)

공캐키가 달라지면 계산법에 의해서 개인키도 같이 달라지므로, 어느 하나만 일방적으로 만들 순 없다.  
**자바에는 키 생성 및 암복호화를 java.crypto, java.security 패키지에 제공한다.**

설명에도 나와있듯이 암호화 하는 키를 개인키(public key)라 하고, 복호화 하는 키를 개인키(private key)라고 하는데, 개인키는 여기저기 공개해도 상관없지만 개인키는 데이터를 볼 대상만이 가지고 있어야 한다.  
그래서 데이터를 암호화 하는건 아무나 할 수 있어도 복호화는 특정 대상만 할 수 있게 해서, 누군가 데이터를 중간에 가로채도 실제 데이터를 볼 수 없게 해놨다.

RSA는 반대 방향의 암복호화도 가능한데, 개인키로 암호화, 공개키로 복호화도 가능하다.
이렇게 역방향으로 사용하는 방식이 공인인증서에 사용된다.

### 소스 
추가로 필요한 라이브러리는 없음
```java
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 암호화
 *
 * @author https://sunghs.tistory.com
 * @see <a href="https://github.com/sunghs/java-utils">source</a>
 */
public class Rsa {

    private static final Charset ENCODING_TYPE = StandardCharsets.UTF_8;

    private static final String INSTANCE_TYPE = "RSA";

    private final Class<?> keyClass;

    private final Key keyInstance;

    private Cipher cipher;

    public Rsa(final Key key) {
        if (key instanceof PublicKey) {
            keyClass = PublicKey.class;
            keyInstance = key;
        } else if (key instanceof PrivateKey) {
            keyClass = PrivateKey.class;
            keyInstance = key;
        } else {
            throw new ClassCastException("unknown key class");
        }

        try {
            cipher = Cipher.getInstance(INSTANCE_TYPE);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * key 객체 String 형으로 인코딩
     *
     * @param key key
     * @return String encodedKey
     */
    public static String encodeKey(final Key key) {
        return new String(Base64.getEncoder().encode(key.getEncoded()));
    }

    /**
     * String 으로 인코딩 된 private key 를 객체로 변환
     *
     * @param encodedKey encodedKey
     * @return PrivateKey key
     */
    public static PrivateKey decodePrivateKey(final String encodedKey) {
        try {
            byte[] bKey = Base64.getDecoder().decode(encodedKey.getBytes(ENCODING_TYPE));
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(bKey);
            KeyFactory keyFactory = KeyFactory.getInstance(INSTANCE_TYPE);
            return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * String 으로 인코딩 된 public key 를 객체로 변환
     *
     * @param encodedKey encodedKey
     * @return PublicKey key
     */
    public static PublicKey decodePublicKey(final String encodedKey) {
        try {
            byte[] bKey = Base64.getDecoder().decode(encodedKey.getBytes(ENCODING_TYPE));
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(bKey);
            KeyFactory keyFactory = KeyFactory.getInstance(INSTANCE_TYPE);
            return keyFactory.generatePublic(x509EncodedKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * PublicKey, PrivateKey 생성
     *
     * @param size 생성될 키의 지정 값 (비트 수), 1024, 2048 권장
     * @return KeyPair
     */
    public static KeyPair generateKey(int size) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(INSTANCE_TYPE);
            keyPairGenerator.initialize(size);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String encrypt(final String str) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        if (keyClass.isAssignableFrom(PublicKey.class)) {
            cipher.init(Cipher.ENCRYPT_MODE, keyInstance);
            byte[] encrypted = cipher.doFinal(str.getBytes(ENCODING_TYPE));
            return new String(Base64.getEncoder().encode(encrypted), ENCODING_TYPE);
        } else {
            throw new ClassCastException("not public key set");
        }
    }

    public String decrypt(final String str) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        if (keyClass.isAssignableFrom(PrivateKey.class)) {
            cipher.init(Cipher.DECRYPT_MODE, keyInstance);
            byte[] decrypted = Base64.getDecoder().decode(str.getBytes(ENCODING_TYPE));
            return new String(cipher.doFinal(decrypted), ENCODING_TYPE);
        } else {
            throw new ClassCastException("not private key set");
        }
    }
}
```

하나의 객체로 암복호화를 하기보다는 한쪽에서는 암호화를 하고 한쪽에서는 복호화를 하므로
객체별로 암호화용, 복호화용으로 쓸 수 있도록 해놓았다. 조금 변경하면 하나의 객체가 암복호화를 할 수 있도록 할 수 있을 것이다.


### 키 생성 테스트
generateKey의 인자는 1024, 2048을 추천한다. (RSA-1024, RSA-2048)
```java
@Test
public void createKeyPair() {
    KeyPair keyPair = Rsa.generateKey(1024);
    Optional.ofNullable(keyPair).ifPresent(k -> {
        PublicKey publicKey = k.getPublic();
        PrivateKey privateKey = k.getPrivate();

        log.info("public : {}", Rsa.encodeKey(publicKey));
        log.info("private : {}", Rsa.encodeKey(privateKey));
    });
}
```
### 결과
```
INFO sunghs.java.utils.cipher.RsaTest - public : MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCjJvSILNVXQJSjO6Qm98u4MNp9nNPmz1XhGIUC8jGoksMEzTcQgeJGBWL0Q19AzJUdS4tWTTUCVQooEwk9/HIiqsGXBwP1hgsOZlRGddwXqxdvBpAmnOCZOh2pYMCyRtXs5OYOMnw6CaYNmiRSRWsd2/AvjrX8pKadwZiknzjbpwIDAQAB


INFO sunghs.java.utils.cipher.RsaTest - private : MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKMm9Igs1VdAlKM7pCb3y7gw2n2c0+bPVeEYhQLyMaiSwwTNNxCB4kYFYvRDX0DMlR1Li1ZNNQJVCigTCT38ciKqwZcHA/WGCw5mVEZ13BerF28GkCac4Jk6HalgwLJG1ezk5g4yfDoJpg2aJFJFax3b8C+Otfykpp3BmKSfONunAgMBAAECgYBE11oU334Be/F70t2Xx7UA+jQnDnZnDJM7EIKHVLRZYdvB+elDINreGsW/NXJKwEgm/UpE1v0IB+PqNuYObqelhplHjtUibfDUdwH1SAdMpa9LREC0MXk9nhaZ9d6p5Gj9ZUWkk75f7UCfhXJ0Iml7I5ARV1LlB4i9qvUNb7RPmQJBAM016GTBvP9p2XJzUJlRgqOrtP6RIg8jm1U7L4k43Tp+rt/jBDwQA8ygwweUr2GXFQfdlxwifaBGqRoYul5hWL0CQQDLiDyk5mIVHrxm4b6fIWHHdfAxOs7zWtByD5G2fmakPYbIIjXp/X6ECi5wJECOoABzK+zWCZeG9uYTjonUGsYzAkBcrk8ySmnwtT63OSuawzyMbT2Gh8fpLHy4Rs3WXO9Vvud+SIqeEeGVZroOz3FSUyj1b3gTBeTVIXS4S5jIjZDFAkB3Z4WseDwyh8Wf1fAvCzaB/f7b4tRmkHCZeejSV3WABVh9MRTQIZeHfzGfOKVnBxc8ehiHuTjcRRzVfFn/xXVhAkEAi6ztiIfv9t6pONiesTq7l3caihGtPHz9SnaWLOTicQQtXlY6phK0Nd95KAUBw7wXejtB3sYfnd5nfm54FiJ9GQ==
```

### 암복호화 테스트
```java
@Test
public void test() {
    KeyPair keyPair = Rsa.generateKey(2048);
    Optional.ofNullable(keyPair).ifPresent(k -> {
        PublicKey publicKey = k.getPublic();
        PrivateKey privateKey = k.getPrivate();

        Rsa rsaForPublic = new Rsa(publicKey);
        Rsa rsaForPrivate = new Rsa(privateKey);

        /*
        평문은 public key로 암호화하고, private key로 복호화 한다.

        RSA 는 반대로 public 복호화, private 암호화가 가능하지만,
        대부분 암호화 하는 키를 공개키(public)로, 복호화 하는 키를 개인키(private) 정의 하기 때문에
        public으로 암호화, private으로 복호화 한다.
            */

        String plain = "test 1234567890 가나다라마바사아자차카타파하";

        try {
            String encrypted = rsaForPublic.encrypt(plain);
            log.info("encrypted : {}", encrypted);
            String decrypted = rsaForPrivate.decrypt(encrypted);
            log.info("decrypted : {}", decrypted);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    });
}
```
### 결과
```
INFO sunghs.java.utils.cipher.RsaTest - encrypted : B2e7Y+II7eZtgOC7H1QULUnJbm2sme7KInVRBFSEeeA0nymC5zqMGNSr+eH3trRqkSCF03WS3KyUvOPyUVc8nLkGhxixUJcJvBB89guiw8oDoAnFxShSXGobWB12VOV2rNPj65gerhNe4hoKX3ACEbswVoxHnVfStSVVYsDsu2RInePmunHq1VPOBJog/Z+QOne8NE4vXL7dac8RrwAaw1aDiC2LaVhv0U4hbu/t3ufF7ezi+ZendGZUl/LlONeJpVxTIOerZM6ztWvTBuWQgrkpOywWo5DRcq/aFFzD4hXJEMFCw0NiUzwUasukFPhe10ScAvnLn0B4FoyX8/CFIw==


INFO sunghs.java.utils.cipher.RsaTest - decrypted : test 1234567890 가나다라마바사아자차카타파하
```