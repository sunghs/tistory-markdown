### 공통으로 사용할 로직
```java
public class AsyncTest {
    
    public static final Logger log = LoggerFactory.getLogger(AsyncTest.class);

    public CompletableFuture task;

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void log(String s) {
        log.info(s);
    }

    public static void main(String[] args) {
        AsyncTest asyncTest = new AsyncTest();
        asyncTest.asyncTest();
    }

    public CompletableFuture asyncTest() {
        //TODO
    }
}
```

### public <U\> CompletableFuture<U\> thenApply(Function<\? super T,? extends U\> fn)

thenApply 메소드는 CompletableFure같은 타입의 CompletableFurue를 다시 반환하는 메소드로 Builder 패턴을 생각하면 된다. 자기 자신을 반환하므로, 메소드 체이닝을 통해서 순서대로 작성이 가능하다.

```java
public CompletableFuture asyncTest() {
    task = CompletableFuture.supplyAsync(() -> {
        log("Start");
        sleep(5000);
        log("end");
        return "OK";
    }).thenApply(result -> {
        log("apply1 : " + result);
        return "apply1 OK";
    }).thenApply(result -> {
        log("apply2 : " + result);
        return "apply2 OK";
    }).thenApply(result -> {
        log("apply3 : " + result);
        return "apply3 OK";
    });
    return task;
}
```

#### 로그
```xml
18:03:05.541 [ForkJoinPool.commonPool-worker-3] INFO sunghs.async.AsyncTest - Start
18:03:10.546 [ForkJoinPool.commonPool-worker-3] INFO sunghs.async.AsyncTest - end
18:03:10.594 [ForkJoinPool.commonPool-worker-3] INFO sunghs.async.AsyncTest - apply1 : OK
18:03:10.595 [ForkJoinPool.commonPool-worker-3] INFO sunghs.async.AsyncTest - apply2 : apply1 OK
18:03:10.597 [ForkJoinPool.commonPool-worker-3] INFO sunghs.async.AsyncTest - apply3 : apply2 OK
18:03:10.597 [main] INFO sunghs.async.AsyncTest - apply3 OK
```
같은 ForkJoinPool Thread에서 처리 되는걸 볼 수 있다.


### public CompletableFuture<Void\> thenAccept(Consumer\<? super T\> action)

thenApply 메소드와 다르게 CompletableFuture가 Void 클래스를 반환하므로, return 값이 없으며 체이닝은 가능해도, Comsumer에 아무것도 전달되지 않기때문에, 첫 호출 이후에는 할 수 있는게 없다.

```java
public CompletableFuture asyncTest() {
    task = CompletableFuture.supplyAsync(() -> {
        log("Start");
        sleep(5000);
        log("end");
        return "OK";
    }).thenAccept(result -> log("accept1 : " + result))
        .thenAccept(result -> log("accept2 : " + result))
        .thenAccept(result -> log("accept3 : " + result));
    return task;
}
```

#### 로그
```xml
18:10:34.080 [ForkJoinPool.commonPool-worker-3] INFO sunghs.async.AsyncTest - Start
18:10:39.085 [ForkJoinPool.commonPool-worker-3] INFO sunghs.async.AsyncTest - end
18:10:39.137 [ForkJoinPool.commonPool-worker-3] INFO sunghs.async.AsyncTest - accept1 : OK
18:10:39.139 [ForkJoinPool.commonPool-worker-3] INFO sunghs.async.AsyncTest - accept2 : null
18:10:39.141 [ForkJoinPool.commonPool-worker-3] INFO sunghs.async.AsyncTest - accept3 : null
18:10:39.141 [main] INFO sunghs.async.AsyncTest - null
```

### CompletableFuture 합성
### public <U\> CompletableFuture<U\> thenCompose(Function<? super T, ? extends CompletionStage<U\>\> fn)

thenCompose는 두개의 CompletableFuture를 합성한다.  
CompletionStage는 CompletableFuture가 종료된 후 반환하는 타입인데, Function 함수형 인터페이스의 첫 인자는 Future의 선언타입이 되고, 두번째 인자는 thenCompose를 실행하는 CompletableFuture와 같은 타입의 완료된 CompletionStage가 된다.

thenCompose는 CompletionStage이 반환될때까지 blocking 하고 이후 실행된다.

**5초가 걸리는 task1, 15초가 걸리는 task2**
```java
public CompletableFuture<String> task1() {
    return CompletableFuture.supplyAsync(() -> {
        log("Start 1");
        sleep(5000);
        log("end 1");
        return "OK 1";
    });
}

public CompletableFuture<String> task2() {
    return CompletableFuture.supplyAsync(() -> {
        log("Start 2");
        sleep(15000);
        log("end 2");
        return "OK 2";
    });
}
```

**task들을 호출하는 method, 이 메소드에서 compose된다.**
```java
public void asyncTest(CompletableFuture<String> t1, CompletableFuture<String> t2) {
    //t1 5초걸림
    //t2 15초 걸림

    t1.thenCompose(s -> t2.thenAccept(act -> {
        log(s + ", " + act);
    })).join();
}
```

이 상황에서 t1의 thenCompose가 실행되는 시점은 t2.thenAccept가 반환되는 시점인 15초 이후이다.

#### 로그
```xml
18:24:12.603 [ForkJoinPool.commonPool-worker-3] INFO sunghs.async.AsyncTest - Start 1
18:24:12.603 [ForkJoinPool.commonPool-worker-5] INFO sunghs.async.AsyncTest - Start 2
18:24:17.608 [ForkJoinPool.commonPool-worker-3] INFO sunghs.async.AsyncTest - end 1
18:24:27.608 [ForkJoinPool.commonPool-worker-5] INFO sunghs.async.AsyncTest - end 2
18:24:27.684 [ForkJoinPool.commonPool-worker-5] INFO sunghs.async.AsyncTest - OK 1, OK 2
```
t1은 24분 17초에 끝났지만 t2의 결과를 기다리고, 24분 27초에 완료 된 이후 thenCompose를 실행한다.


### public static CompletableFuture<Void\> allOf(CompletableFuture<?>... cfs)
가변 파라미터로 CompletableFuture들을 받고 전부 완성되면 진행.
action을 정해주면 모든 인자로 전달한 CompletableFuture가 끝난 이후에 실행하게 된다.
```java
public static void main(String[] args) {
    AsyncTest asyncTest = new AsyncTest();
    asyncTest.asyncTest();

    //데몬형태인 forkjoinpool 종료 방지
    sleep(10000);
}

public CompletableFuture task1() {
    return CompletableFuture.supplyAsync(() -> {
        log("START 1");
        sleep(6000);
        log("END 1");
        return "OK1";
    });
}

public CompletableFuture task2() {
    return CompletableFuture.supplyAsync(() -> {
        log("START 2");
        sleep(2000);
        log("END 2");
        return "OK2";
    });
}

public CompletableFuture asyncTest() {
    CompletableFuture.allOf(task1(), task2()).thenAccept(aVoid -> log("모든 작업 완료"));
    return null;
}
```
설명을 해보자면
1. main 함수에서 실행되며, asyncTest라는 함수를 호출한다.  
2. asyncTest 함수는 task1()과 task2()를 인자로 받는 allOf 메소드를 호출한다.  
3. task1()은 작업이 6초, task2()는 작업이 2초 걸린다. 
4. 그렇다면 실제로 allOf가 실행되는 시점은 두 task의 END 로그가 출력된 이후여야 한다. (6초 이후)

#### 로그
```xml
22:59:51.295 [ForkJoinPool.commonPool-worker-3] INFO sunghs.async.AsyncTest - START 1
22:59:51.295 [ForkJoinPool.commonPool-worker-5] INFO sunghs.async.AsyncTest - START 2
22:59:53.301 [ForkJoinPool.commonPool-worker-5] INFO sunghs.async.AsyncTest - END 2
22:59:57.300 [ForkJoinPool.commonPool-worker-3] INFO sunghs.async.AsyncTest - END 1
22:59:57.300 [ForkJoinPool.commonPool-worker-3] INFO sunghs.async.AsyncTest - 모든 작업 완료
```
### public static CompletableFuture<Object\> anyOf(CompletableFuture<?\>... cfs)
allOf와 입력 파라미터는 같지만, 어느 하나라도 끝나게 되면 이벤트가 발생한다.  
나머지 소스는 allOf와 동일하고 asyncTest()에서 allOf가 anyOf로 바뀌었다.
```java
public CompletableFuture asyncTest() {
    CompletableFuture.anyOf(task1(), task2()).thenAccept(aVoid -> log("끝난게 있다."));
    return null;
}
```

#### 로그
```xml
23:04:31.794 [ForkJoinPool.commonPool-worker-5] INFO sunghs.async.AsyncTest - START 2
23:04:31.794 [ForkJoinPool.commonPool-worker-3] INFO sunghs.async.AsyncTest - START 1
23:04:33.800 [ForkJoinPool.commonPool-worker-5] INFO sunghs.async.AsyncTest - END 2
23:04:33.800 [ForkJoinPool.commonPool-worker-5] INFO sunghs.async.AsyncTest - 끝난게 있다.
23:04:37.800 [ForkJoinPool.commonPool-worker-3] INFO sunghs.async.AsyncTest - END 1
```
task2() 시작 2초 후에 끝난 뒤, 실행되었다.


### public CompletableFuture<T\> exceptionally(Function<Throwable, ? extends T\> fn)
Function<T, R>의 apply 메소드가 R을 반환하므로, 메소드 체이닝에 연결된 제네릭을 반환하면 된다.  
task 실행도중 Exception이 발생할 경우 실행할 이벤트를 구현한다.

```java
public CompletableFuture task1() {
        return CompletableFuture.supplyAsync(() -> {
            log("START 1");
            sleep(6000);
            throw new RuntimeException("time over");
        }).exceptionally(throwable -> {
            log("error! : " + throwable.getMessage());
            return "ERROR";
        });
    }

    public CompletableFuture task2() {
        return CompletableFuture.supplyAsync(() -> {
            log("START 2");
            sleep(2000);
            log("END 2");
            return "OK2";
        });
    }

    public CompletableFuture asyncTest() {
        CompletableFuture.anyOf(task1(), task2()).thenAccept(aVoid -> log("끝난게 있다."));
        return null;
    }
```

task1()은 무조건 6초후 RuntimeException이 발생한다.

#### 로그
```xml
23:44:34.247 [ForkJoinPool.commonPool-worker-3] INFO sunghs.async.AsyncTest - START 1
23:44:34.247 [ForkJoinPool.commonPool-worker-5] INFO sunghs.async.AsyncTest - START 2
23:44:36.252 [ForkJoinPool.commonPool-worker-5] INFO sunghs.async.AsyncTest - END 2
23:44:36.253 [ForkJoinPool.commonPool-worker-5] INFO sunghs.async.AsyncTest - 끝난게 있다.
23:44:40.276 [ForkJoinPool.commonPool-worker-3] INFO sunghs.async.AsyncTest - error! : java.lang.RuntimeException: time over
```