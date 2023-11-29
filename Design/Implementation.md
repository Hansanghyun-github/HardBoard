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

---

인텔리제이 디버깅 단축키 정리

| inst                   | explain                                            |
|------------------------|----------------------------------------------------|
| resume(f9)             | 다음 브레이킹 포인트까지 실행(없으면 끝)                            |
| step into(f7)| 해당 메서드 내로 들어 감                                     |
|step over(f8)| 다음 코드로 이동                                          |  
|step out(shift+f8)| step into로 들어간 메서드 밖으로 나옴 & 해당 코드 실행               |  
|evaluate expression(alt+f8)| 실행된 상태에서 내가 원하는 코드 실행(실제로 실행되기 때문에 코드에 영향을 줄 수 있음) |

브레이킹 포인트에 condition을 줄 수 있음  
(기존 브레이킹 포인트는 무조건 멈추지만, condition을 설정하면, 내가 원하는 조건에서만 멈추게 할 수 있다)

---

Report Entity에서 post와 comment를 따로 구별해야 할까?

1. 구별하면 entity 두개라서 귀찮치만, 확실해서 헷갈리지 않음
2. 구별하지 않으면 간단하지만, 검색할때마다 post인지 comment인지 확인해야 함

> 구별하지 않는 걸로
> 
> 엔티티 두개하는 것이 더 귀찮을 것으로 예상

---

jpa 관련 오류가 터졌을 때는, 해당 코드를 바로 보지 말고  
EntityManager를 통해, 일일이 flush 해주면서 정확히 어디서 오류가 터졌는지 확인해주자

> 트랜잭션 때문에, 정확히 어디서 오류가 터졌는지 확인하기 힘들다.

---

회원가입 수행할 때, 이메일 또는 닉네임이 중복될 수 있음  
이를 DataIntegrityViolationException을 이용해서 예외 처리하려고 했는데,  
이메일이 중복된 것인지, 닉네임이 중복된 것인지 확인이 힘들다.  
(에러 메시지를 통해 확인 가능하지만 API Response로 보내주기는 별로인 듯하다)

> DataIntegrityViolationException으로 처리하지 않고,  
> DB에 저장하기 전에, 해당 이메일이나 닉네임에 해당하는 유저있는지 미리 검사한다.
>
> -> 에러 처리하기 편하다.

---

단일 column에 유니크 제약조건 추가하면,  
jpa로 저장할 때 바로 exception 터지지만, (DataIntegrityViolationException)  
복수 column에 유니크 제약조건 추가하면,  
jpa로 저장할 때 바로 exception 터지지 않고, 트랜잭션이 끝날 때(or flush) exception이 터진다. (PersistenceException)

---

```
List<BlockResponse> collect = (List<BlockResponse>) list.stream().map(d ->
        {
            try {
                return objectMapper.readValue(objectMapper.writeValueAsString(d), BlockResponse.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        })
        .collect(Collectors.toList());
```

왜 굳이 캐스팅을 해줘야 하나?

---

테스트 하고 출력 결과 볼 때, 일일이 pageDown 눌러서 내려가는 거 너무 귀찮다.  
빠르게 가는 법 없나?