## [JAVASCRIPT] eval - 문자열을 코드로 사용하는 함수

eval 함수는 문자열을 코드로 인식시켜주는 함수이다.
어떤 객체에 값 또는 object를 넣어야 하는데 동적으로 움직일 때 사용하면 편리하다.

#### 문자열을 넣을 수 있다.
```javascript
function exam() {
	var d;
	eval("d = 'test'");
	
	console.log(d);
}
```

Console에 찍히는 값은 test 이다.

### 

#### 객체도 넣을 수 있다.

예를들어 파라미터에 따라 다른 객체의 object를 넣어줘야 하는경우 매번 분기문을 태웠을 것이다.
```javascript
function getInstance(type) {
	var d;
	
	if(type == 'A') {
		d = OBJECT.A;
	}
	else if(type == 'B') {
		d = OBJECT.B;
	}
	else if(type == ...) {
		d = OBJECT ...
	}
	
	return d;
}
```
이방법은 type이 추가 될 때 마다 getInstance 함수를 매번 고쳐줘야 한다는 것이다.
이러한 방법을 eval로 해결 할 수 있다.

```javascript
function getInstance(type) {
	var d;
	
	eval("d = OBJECT." + type);
	
	return d;
}
```

이렇게 사용하면 type이 추가되어도 함수를 고치지 않아도 된다.

### 

#### 함수도 실행 가능하다.

```javascript
eval("alert('hello')");
```

### 

---
eval 함수는 실행 시점에 컴파일이 한번 더 되므로 속도가 느리고,
일부로 오류를 발생시켜 보안에 취약 할 수 있다. (stackTrace에 의한 서버 경로 노출 등)
꼭 필요한 곳에만 써야한다.