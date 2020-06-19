## [JQUERY] \$.getJSON 사용 시 한글 깨지는 현상 해결

예를들어 이런 JQuery가 있을 떄

```javascript
$.getJSON(
    'URL.do',
    {
        id : id,
        name : name
    },
    function(result) {
        draw(result);
    }
);
```

servlet (controller 단 이후) 에 id 와 name 파라미터를 받았을 때
파라미터 값의 한글이 깨지는 현상이 있다면 URL ENCODER, DECODER를 이용하면 된다.

### 

```javascript
$.getJSON(
    'URL.do',
    {
        id : id,
        name : encodeURIComponent(name)
    },
    function(result) {
        draw(result);
    }
);
```

Controller 단의 받을 때는 다시 decode 해준다.
```java
// 한글이 안들어오는 id는 encode 안해줬으니 그냥 받아도 된다.

String id = params.get("id");
String name = URLDecoder.decode(params.get("name"));
```