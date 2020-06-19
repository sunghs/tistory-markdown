### HttpServletRequest Parameter 추가

HttpServletRequest 클래스에는 getParameter만 있고 setParameter는 없다.  
클라이언트 단에서 날아온 값으로 서블릿에서는 setAttribute 함수밖에 없으며 setAttribute 값은 getAttribute로 밖에 꺼내올 수 없는데, setAttribute와 setParameter에는 서버에서 세팅했는지, 클라이언트에서 세팅했는지에 대한 차이가 있다.

따라서 다른 API / 라이브러리 등에서 getParamter를 하기전에 이미 들어있는 값을 바꾸려면 httpServletRequest를 전부 복사해서 다시 만들어야 한다.

#### HttpServletRequest를 전부 복사해서 갈아끼우는 Class

```
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

class ModifiableHttpServletRequest extends HttpServletRequestWrapper {

    private HashMap<String, Object> params;

    @SuppressWarnings("unchecked")
    public ModifiableHttpServletRequest(HttpServletRequest request) {
        super(request);
        this.params = new HashMap<String, Object>(request.getParameterMap());
    }

    public String getParameter(String name) {
        String returnValue = null;
        String[] paramArray = getParameterValues(name);
        if (paramArray != null && paramArray.length > 0) {
            returnValue = paramArray[0];
        }
        return returnValue;
    }

    @SuppressWarnings("unchecked")
    public Map getParameterMap() {
        return Collections.unmodifiableMap(params);
    }

    @SuppressWarnings("unchecked")
    public Enumeration getParameterNames() {
        return Collections.enumeration(params.keySet());
    }

    public String[] getParameterValues(String name) {
        String[] result = null;
        String[] temp = (String[]) params.get(name);
        if (temp != null) {
            result = new String[temp.length];
            System.arraycopy(temp, 0, result, 0, temp.length);
        }
        return result;
    }

    public void setParameter(String name, String value) {
        String[] oneParam = { value };
        setParameter(name, oneParam);
    }

    public void setParameter(String name, String[] value) {
        params.put(name, value);
    }
}
```

#### 사용방법

```
ModifiableHttpServletRequest m = new ModifiableHttpServletRequest(request);
m.setParameter("key1", "value1");

//TODO

request = (HttpServletRequest)m;

```

기존 request 객체에 없던 key1 키에 대한 value1의 값이 포함되게 된다.  
원래 존재하던 request의 key, value를 hashMap에 복사하고, 다시 request에 덮어씌우는 방식이다.