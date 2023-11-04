### 프로젝트 구조

api
- ControllerAdvice.java
- ApiResponse.java // ControllerAdvice에서 사용하는 response
- controller
  - domain1
    - Domain1Controller.java
    - request
      - CreateDomain1Request.java // 클라에게 받는 request - validation
  - domain2
    - Domain2Controller.java
    - request
      - CreateDomain2Request.java
- service
  - domain1
    - Domain1Service.java
    - request
      - CreateDomain1ServiceRequest.java // 컨트롤러로부터 받는 request
    - response
      - Domain1Response.java // 클라에게 보내는 response
  - domain2
    - Domain2Controller.java
    - request
      - CreateDomain2ServiceRequest.java
    - response
      - Domain2Response.java

domain
- domain1
  - Domain1.java
  - Domain1Repository.java
- domain2
  - Domain2.java
  - Domain2Repository.java

auth
- config
  - SecurityConfig.java

exception
- exception1.java

---

### 테스트는 어떻게 할까

Controller, Service, Repository

1. Repository는 단위 테스트로 진행

(클래스에 @SpringBootTest, 테스트 환경 분리하려면 @ActiveProfiles("test"))
(Repository에는 @Autowired로 주입)

> 스프링 데이터 JPA를 사용하기 떄문에, 기존에 주어지는 메서드들은 검증x
> 
> 내가 새로 만든 메서드들은 검증 필요

2. Service는 Repository와 함께 통합 테스트로 진행

(Repository와 똑같이 진행, 필요한 의존성들 @Autowired)

3. Controller는 Service와 Repository를 Mocking하고 테스트한다.

> 단위 테스트 느낌으로

목킹 테스트를 사용할때는 클래스에 `@WebMvcTest`를 붙이고, <br>
필드에 `MockMvc`를 @Autowired 받아야 한다.

그리고 목킹 처리할 필드(지금은 Controller가 의존하는 Service들)에 @MockBean을 선언 해줘야 한다. <br>

> @WebMvcTest(controllers = ~.class) - 컨트롤러 관련 빈들만 올릴 수 있는 가벼운 테스트 어노테이션
> 
> @MockBean - Mockito 라이브러리의 어노테이션

```java
mockMvc.perform(post("원하는 url").content(~).contentType(~))
    .andExpect(status().isOk()).andExpect(jsonPath("$.필드이름").value(~));

// static import한 상태

// get은 content, contentType 없어도 됨
```

> 컨트롤러는 클라 입력에 대한 검증을 진행하는데<br>
> 검증을 진행하더라도 한번에 한 레이어에서 검증을 진행할 필요는 없다
> 
> 어떤 필드에 @NotBlank와 @Max(20) 이 있다고 하면
> 
> @NotBlank는 컨틀로러에서 하는게 맞지만, @Max(20)까지 컨트롤러에서 할 필요는 없다.
> 
> -> 근데 이건 잘 모르겠다. 일단은 컨트롤러에서 다 한다.

---

### 의존성 관리

Controller -> Service -> Repository

컨트롤러는 서비스만 의존하도록

서비스는 리포지토리만 의존하도록

> Dto도 마찬가지

---

### private @Builder, of(create) 메서드

생성자에 @Builder를 달아주는 대신, 접근 제어자를 private로 설정하고,<br>
해당 객체 인스턴스 생성을 위한 of 메서드를 생성한다.<br>
(여기서 of(create) 메서드가 생성자를 이용해서 인스턴스 생성)<br>
(of(create) 메서드는 static 메서드)

-> 해당 객체 인스턴스를 만들기 위해 생성자가 아닌, of(create) 메서드만을 이용하도록 유도<br>

> 여기서 of는 어떤 객체를 다른 객체로 변환하기 위해 사용함

```java
class ProductResponse{ 
    @Builder
    private ProductResponse() { }
  
    public static ProductResponse of(Product product) { /* 생성자 빌더 이용해서 인스턴스 생성*/ }
  
}
```

---

### 생성일과 수정일은 BaseEntity를 통해 한번에 관리

---

### 조회용 Service 메서드들은 꼭 @Transactional(readOnly = true) 달아주자