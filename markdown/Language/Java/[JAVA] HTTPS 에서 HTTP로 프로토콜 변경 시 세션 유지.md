### HTTPS 에서 HTTP로 프로토콜 변경 시 세션 유지

#### 먼저 HTTPS로 들어온 세션을 쿠키로 만들어 줄 Wrapper Class가 필요하다.

```java
import java.util.Collections; 
import java.util.Enumeration; 
import java.util.HashMap; 
import java.util.Map; 
 
import javax.servlet.http.Cookie; 
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpServletRequestWrapper; 
import javax.servlet.http.HttpServletResponse; 
import javax.servlet.http.HttpSession; 
 
public class ModifiableHttpServletRequest extends HttpServletRequestWrapper { 
 
    private HashMap<String, Object> params; 

    private HttpServletResponse response = null; 
 
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
 
    public Map<? extends String, ? extends Object> getParameterMap() { 
        return Collections.unmodifiableMap(params); 
    } 
 
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
 
    public void setResponse(HttpServletResponse response) { 
        this.response = response; 
    } 
 
    public HttpSession getSession() { 
        HttpSession session = super.getSession(); 
        processSessionCookie(session); 
        return session; 
    } 
 
    public HttpSession getSession(boolean create) { 
        HttpSession session = super.getSession(create); 
        processSessionCookie(session); 
        return session; 
    } 
 
    private void processSessionCookie(HttpSession session) { 
        if (null == response || null == session) 
            return; 
 
        Object cookieOverWritten = getAttribute("COOKIE_OVERWRITTEN_FLAG"); 
        if (null == cookieOverWritten && isSecure() && isRequestedSessionIdFromCookie() && session.isNew()) { 
            Cookie cookie = new Cookie("JSESSIONID", session.getId()); 
            cookie.setMaxAge(-1); 
            String contextPath = getContextPath(); 
            if ((contextPath != null) && (contextPath.length() > 0)) { 
                cookie.setPath(contextPath); 
            } else { 
                cookie.setPath("/"); 
            } 
            response.addCookie(cookie); 
            setAttribute("COOKIE_OVERWRITTEN_FLAG", "true"); 
        } 
    } 
}
```
https URL을 타고 들어오는 경우, processSessionCookie 메소드를 통해 현재 sessionID의 세션 내용을 쿠키로 굽는다.

#### Wrapper Class를 실행시켜 줄 Filter Class
FilterClass가 ModifiableHttpServletRequest 메소드를 호출한다.
```java
import java.io.IOException; 
 
import javax.servlet.Filter; 
import javax.servlet.FilterChain; 
import javax.servlet.FilterConfig; 
import javax.servlet.ServletException; 
import javax.servlet.ServletRequest; 
import javax.servlet.ServletResponse; 
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpServletResponse; 
 
public class HttpsFilter implements Filter { 
 
    @Override
    public void destroy() { 
    } 
 
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException { 
        ModifiableHttpServletRequest modiRequest = new ModifiableHttpServletRequest((HttpServletRequest)request); 
        modiRequest.setParameter("HTTPS", "TRUE"); 
        modiRequest.setResponse((HttpServletResponse)response); 
        filterChain.doFilter(modiRequest,  response); 
    } 
 
    @Override
    public void init(FilterConfig arg0) throws ServletException { 
    } 
}
```
doFilter 메소드를 WAS에서 실행 해준다.

#### web.xml에 SSL 요청 시 실행해 줄 filter를 등록
```xml
<filter> 
    <filter-name>SSLFilter</filter-name> 
    <!-- HttpsFilter 클래스의 패키지명 --> 
    <filter-class>a.b.c.HttpsFilter</filter-class> 
</filter> 
<filter-mapping> 
    <filter-name>SSLFilter</filter-name> 
    <!-- URL 패턴 --> 
    <url-pattern>*.do</url-pattern> 
</filter-mapping>
```
jSessionID에 대한 변조 필터가 있다면 쿠키 박제 이후 실행가능 하도록 해당 필터를 더 위에 달아줘서 실행순서를 맞춰줘야 한다. (가장 먼저 실행되도록)

####  

이후 SSL (https) 요청 시 http로 Redirect 시키고, JsessionID가 같으므로 Cookie의 내용을 가져와 세션으로 사용. 정상적으로 https -> redirect -> http 요청이 이루어지게 된다.
