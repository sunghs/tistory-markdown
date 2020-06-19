## [JQUERY] 메소드 이벤트 발생시키기

예를들어 checkbox의 change 이벤트를 등록 했을 때
다른 이벤트에서 checkbox의 change 이벤트를 발생시키는 방법

### EX) checkbox 태그

```html
<input type="checkbox" id="box1" name="checkboxFamily"> ... 
<input type="checkbox" id="box2" name="checkboxFamily"> ... 
<input type="checkbox" id="box3" name="checkboxFamily"> ... 
<input type="checkbox" id="box4" name="checkboxFamily"> ... 
```

### EX) radio button 태그

```html
<input type="radio" id="radio1" name="radioFamily" value="S"> ... 
<input type="radio" id="radio2" name="radioFamily" value="H"> ... 
<input type="radio" id="radio3" name="radioFamily" value="K"> ... 
```
---

*checkboxFamily라는 name의 checkbox의 checked가 변경 되었을 때 
console을 출력하고 아래 TODO 로직을 실행하는 이벤트*
```javascript
$("input:checkbox[name='checkboxFamily']").change(function() {
    console.log("checkbox changed .. value : " + $(this).val());
    //TODO
});
```
위의 경우 box1 ~ box4의 checkbox가 체크되거나 해제 될때 마다 이벤트가 발생할 것이다.

---

라디오 버튼을 클릭했을 때 checkbox를 자동으로 체크하게 되어있다면 보통
```javascript
$("input:radio[name=radioFamily]").click(function() {
    $("input:checkbox[id='box1']").prop("checked", true);
});
```
이런식으로 구현되어 있을텐데, checked 를 입력했다고 checkboxFamily의 change 이벤트가 발생하지 않는다.
change 메소드를 실행시켜 이벤트를 태울 수 있도록 해야한다.

```javascript
$("input:radio[name=radioFamily]").click(function() {
    $("input:checkbox[id='box1']").prop("checked", true);
    $("input:checkbox[id='box1']").change();
});
```
이렇게 구현되면 radio 버튼 클릭을 하면 box1이 체크되고, console 및 TODO 로직을 실행한다.
