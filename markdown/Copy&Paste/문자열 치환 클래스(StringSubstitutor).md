## 문자열 치환 클래스 (StringSubstitutor, StringMapper)
### 문자열 안에 있는 약속된 기호 (${key} ${map1} 등등..)을  map에서 key를 가져와 value로 바꿔주는 클래스.  
소스는 github에도 있음.

apache의 commons-text 라이브러리에 StringSubstitutor 클래스를 써도 되는데, StringSubstitutor 클래스는 없는 키는 그대로 ${key} 형태로 남겨 놓는다. 이게 단점이라 직접 구현 했다.  
또는 혹시 라이브러리 추가가 불가능한 환경에서 쓸수 있도록 클래스로 만들어놓음.  

### 코드
*따로 라이브러리 추가는 없음.*
```java
package sunghs.java.utils.string;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 문자열 내에 있는 ${key}로 된 매핑 규칙을 ValueMap의 key로 value를 가져와 치환합니다.
 *
 * @author https://sunghs.tistory.com
 * @see <a href="https://github.com/sunghs/java-utils">source</a>
 */
public class StringMapper {

    public static final String MAPPING_PREFIX = "${";

    public static final String MAPPING_SUFFIX = "}";

    public static final String EMPTY_STRING = "";

    public static final String MAPPING_PATTERN = "(\\$\\{)([a-zA-Z0-9]*)(})";

    static class Cursor {

        private int s;

        private int e;

        void setS(int s) {
            this.s = s;
        }

        void setE(int e) {
            this.e = e;
        }

        int getS() {
            return this.s;
        }

        int getE() {
            return this.e;
        }
    }

    private static Cursor find(String source, int offset) {
        int sid = source.indexOf(MAPPING_PREFIX, offset);
        int eid = source.indexOf(MAPPING_SUFFIX, offset);

        Cursor csr = new Cursor();
        csr.setS(sid);
        csr.setE(eid);
        return csr;
    }

    public static String doReplace(final String str, final Map<String, String> map) {
        int offset = 0;
        String replaced = str;

        while (true) {
            Cursor csr = find(replaced, offset);
            int s = csr.getS();
            int e = csr.getE();
            if (s < 0 || e < 0) {
                break;
            } else {
                String key = replaced.substring(s + MAPPING_PREFIX.length(), e);
                String value = (map.get(key) == null ? EMPTY_STRING : map.get(key));

                int vLen = value.length();
                int oLen = MAPPING_PREFIX.length() + key.length() + MAPPING_SUFFIX.length();
                int diff = vLen - oLen;

                replaced = replaced.replace(MAPPING_PREFIX + key + MAPPING_SUFFIX, value);
                offset = e + diff;
            }
        }
        return replaced;
    }

    public static String doReplaceWithRegEx(final String str, final Map<String, String> map) {
        Pattern pattern = Pattern.compile(MAPPING_PATTERN);
        Matcher matcher = pattern.matcher(str);
        String replaced = str;

        while(matcher.find()) {
            String prefix = matcher.group(1);
            String key = matcher.group(2);
            String suffix = matcher.group(3);
            String value = (map.get(key) == null ? EMPTY_STRING : map.get(key));
            replaced = replaced.replace(prefix + key + suffix, value);
        }
        return replaced;
    }
}

```
두 메소드 똑같은 역할을 한다.  
빈 ${key} 에 대해서는 ""로 치환시킨다.

정규식이 생소하면 doReplace 메소드가 나을 수도 있다.

**예제**
```java
@Test
public void StringMapperTest() {
    Map<String, String> map = new HashMap<>();
    map.put("map1", "123123");
    map.put("aaaaaaaa", "234234");
    map.put("123qweasdzxc", "345345");

    String str = "가가가가가가가나나나나다다다라라라라라${map1}가가가가나나다라라라 " +
            "\r\n" +
            "abcedfg ${aaaaaaaa} test1234567890 zxcv ${123qweasdzxc} " +
            "\r\n" +
            "\r\n" +
            "testtest ${nomapping} << nomapping 없는 부분" +
            "\r\n" +
            "1234567890";

    String doReplaced = StringMapper.doReplace(str, map);

    String doReplacedRegEx = StringMapper.doReplaceWithRegEx(str, map);

    String expected = "가가가가가가가나나나나다다다라라라라라123123가가가가나나다라라라 " +
            "\r\n" +
            "abcedfg 234234 test1234567890 zxcv 345345 " +
            "\r\n" +
            "\r\n" +
            "testtest  << nomapping 없는 부분" +
            "\r\n" +
            "1234567890";

    Assertions.assertEquals(doReplaced, doReplacedRegEx);
    Assertions.assertEquals(doReplaced, expected);
    Assertions.assertEquals(doReplacedRegEx, expected);
}
```