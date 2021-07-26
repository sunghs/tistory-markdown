## 스프링 의존성 주입 3가지 방법

소스는 [여기서](https://github.com/sunghs/spring-example) 볼 수 있습니다.

의존성 주입(Dependency Injection)은 많이 아실거라 생각합니다. Spring 에서 지원하는 의존성 주입은 3가지 방법으로 사용가능합니다.

- Field(필드) 주입
- Setter(수정자) 주입
- Constructor(생성자) 주입

예를들어 주입받아야 하는 Service가 아래와 같은 코드로 되어있다고 하겠습니다.
```java
@Service
public class ExampleService {

    public String businessLogic() {
        return "OK";
    }
}
```

각각의 서비스에서 3가지 주입 방법으로 주입해 보겠습니다.

### 1. Field 주입
필드주입은 Spring 초창기부터 현재까지 계속 사용되는 방법입니다. 멤버 객체에 @Autowired를 붙여 주입받는 방법입니다.
```java
@Service
public class FieldInjectionService {

    @Autowired
    private ExampleService exampleService;

    public void getBusinessLogic() {
        exampleService.businessLogic();
    }
}
```
다들 아는 방법이라 설명할 내용이 없습니다.

다만 Spring 런타임 시 `ExampleService` 객체를 찾아오는 방법은 아래와 같습니다.

1. 타입이 같은 객체를 빈 팩토리에서 검색하고, 객체가 있으면 그 객체를 사용

    타입이 두개 이상인 경우 @Primary, @Qualifier 명시를 통해 더 상세한 객체를 가져옴

2. 객체가 없다면 생성 후 빈 팩토리에 등록 

빈 팩토리에 등록하는 과정은 `AbstractAutowireCapableBeanFactory` 클래스의 `doCreateBean(...)`을 통해 진행하는 것으로 보입니다.

```java
// 메소드 계속 진행

boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences && isSingletonCurrentlyInCreation(beanName));
if (earlySingletonExposure) {
    if (logger.isTraceEnabled()) {
        logger.trace("Eagerly caching bean '" + beanName +
                "' to allow for resolving potential circular references");
    }
    addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
}

// 메소드 계속 진행
```

### 2. Setter 주입
getter/setter 의 그 setter 맞습니다. setter 메소드 위에 @Autowired를 선언하게 되면, setter 메소드의 파라미터에 해당하는 객체를 BeanFactory에서 가져옵니다.

```java
@Service
public class SetterInjectionService {

    private ExampleService exampleService;

    @Autowired
    public void setExampleService(ExampleService exampleService) {
        this.exampleService = exampleService;
    }

    public void getBusinessLogic() {
        exampleService.businessLogic();
    }
}
```
setter 주입에 필요한 `ExampleService`를 찾아 가져오는 방법은 위의 필드 주입 방법과 같습니다.

### 3. Constructor 주입

생성자 주입은 Field, Setter와는 조금 다르게 진행합니다.
위의 두 주입 방법은 Service Bean이 만들어지고, 그 뒤 BeanFactory에서 의존 객체를 가져와 주입하지만, 생성자 주입은 ServiceBean이 만들어지는 시점에서 모든 의존관계를 BeanFactory를 통해 가져와야 합니다.

```java
@Service
public class ConstructorInjectionService {

    private final ExampleService exampleService;

    public ConstructorInjectionService(final ExampleService exampleService) {
        this.exampleService = exampleService;
    }

    public void getBusinessLogic() {
        exampleService.businessLogic();
    }
}
```

이 방법을 통해 발생하는 장점이 있습니다.


**불변 객체를 만들 수 있음**

Field와 Setter에서 사용하는 방법, 즉 @Autowired를 이용하는 방법은 처음에는 FieldInjectionService 또는 SetterInjectionService 객체가 생성되는 시점에서는 `ExampleService`가 null 이었다가 이후 @Autowired를 검색하여 차례차례 BeanFactory에서 가져와 instance 되는 방법일 것입니다. 따라서 해당 객체를 불변 객체로 만들 수 없습니다.

```java
// 이렇게 쓸수 없음
@Autowired
private final ExampleService exampleService;
```

하지만 생성자 주입에서는 가능합니다. 즉 한번 할당된 `exampleService`를 누군가 null로 바꾼다거나, 새로운 instance를 할당한다거나 할 수 없습니다.

**순환참조를 막을 수 있음**

Aservice 에서 Bservice를 주입하고, Bservice에서 Aservice를 주입하는 코드가 있다고 한다면

```java
// AService
@Service
public class AService {

    private final BService bService;

    public AService(BService bService) {
        this.bService = bService;
    }
}

// BService
@Service
public class BService {

    private final AService aService;

    public BService(AService aService) {
        this.aService = aService;
    }
}
```

아마 Bean이 생성되는 시점에서는 이렇게 될것입니다.

```java
new AService(new BService(new AService(new BService(new AService(...)))));
```
즉 Bean 생성시점에 에러가 날 것이므로, 정상적이지 않은 코드임을 알아낼 수 있습니다. 이상없이 서비스가 구동되고 나중에 문제가 생기는 것보다 새 버전의 빌드/배포 오류가 발생해서 기존 버전으로 계속 동작하는게 더 이득일 것입니다.

실제로 서비스 구동 시 아래와 같이 에러가 나면서 실패합니다.

![](./../../static/Framework/CircleRef-fail.png)

**NullPointerException 방지**

NPE를 어떻게 막나 싶을텐데 Field, Setter 주입은 Service 객체가 모두 생성 된 후 주입되므로, 실제 코드가 호출되기 전까지 NPE를 알 수 없습니다. 즉 NPE가 뜨긴 하는데 실제 코드 수행이 되는 순간에 알아챌 수 있습니다.

반면 생성자 주입은 객체 생성 시점에 무조건 주입해야 하므로 BeanFactory에 들어있는지 검사를 해야합니다. 거기서 NPE를 발생시키면 Service 생성이 실패할 것이고, Bean 생성이 실패했으므로 서비스 구동에 실패합니다.


그 외 테스트코드에서 Service 들을 instance 할 때 테스트코드 작성하기 편함 등의 효과가 있습니다. 이는 TestConstructor를 지원하면서 더 편리해졌습니다.

혹시 Lombok을 사용하고 있다면 필요 생성자 자동생성 어노테이션을 통해 간편하게 쓸 수 있습니다.

```java
@Service
@RequiredArgsConstructor
public class ConstructorInjectionService {

    private final ExampleService exampleService;

    public void getBusinessLogic() {
        exampleService.businessLogic();
    }
}
```
