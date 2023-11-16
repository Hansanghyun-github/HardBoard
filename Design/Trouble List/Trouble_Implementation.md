컨트롤러 메서드에서 반환할 때 필드 에러를 관리하면 반환 타입이 애매해진다.

    (Member -> ResponseEntity<Object>)
    에러를 포함하지 않으면 Member만 반환하는 상황에서
    에러일 때의 반환 값은 Member와 달라야 한다. -> ResponseEntity<Object>로 교체 해줘야 했음

> 필드 에러 등, 예상하지 못한 상황은 ControllerAdvice로 처리
> 
> -> 기존 반환 타입 유지할 수 있음

---

컨트롤러에서 objectId를 검증하고 나서, 서비스에서 또 검증할 필요가 있을까?

> 같은 트랜잭션을 쓴다면, 트랜잭션 전파, JPA의 1차 캐시 덕분에 상관없지만,
> 
> 트랜잭션을 따로 쓸 것 같아서, 그냥 서비스에서만 검증 하는 걸로<br>
> & 유효하지 않은 id라면 에러 발생 시키고, ControllerAdvice로 처리 하자

---

커스텀 예외를 만들때 그냥 invalidId를 할까, 아니면 invalid{Object}Id를 할까?

invalidId 장단점<br>
장점: 일괄 처리라서 에러 처리하기 간단함<br>
단점: 프론트 입장에서 어떤 {Object}가 invalid 한지 파악하기 귀찮음

invalid{Object}Id 장단점<br>
장점: 프론트 입장에서 어떤 {Object}가 invalid 한지 파악하기 편함<br>
단점: 일일이 다 에러 처리 해줘야 함

> (나) 백엔드 입장에선 invalidId가 편해서, invalidId로 일괄 처리

---

테스트 할 때, 필드의 접근제어자를 뭘로 해야 하나?

---

컨트롤러 테스트 중 403 에러 & 401 에러

@WebMvcTest를 통해 컨트롤러를 테스트 중이었는데,  
403 에러(1)와 401 에러(2)가 떴다.

> Spring Security에 대한 설정은 @WebMvcTest의 스캔 대상에 해당한다.  
> -> 인증을 위한 권한과 csrf 토큰을 지정해줘야 한다.
> 
> 이 프로젝트는 세션 방식을 사용하지 않으므로 csrf는 안해줘도 된다.

```java
@WithMockUser(roles = "권한명")
```

---

JWT를 이용해서 인증 진행할 때, 토큰에 있는 정보가 맞는지 DB까지 가서 검증을 해야 할까?

검증 하면,   
장점: 토큰을 조작한걸 막을 수 있음  
단점: DB 확인해야 해서, 느려짐   

검증 안하면,  
장점: DB까지 안가기 때문에, 빠름  
단점: 조작한 걸 막을 수 없음  

> DB 검증 안하는 것으로 결정,  
> 사용자 정보를 조작을 해서 인증을 통과 했다는 건, 이미 시크릿 넘버가 뚫렸다는 것  
> 
> JWT의 목적은 Sessionless를 통해 Stateless 하게 웹을 연결하는 것  
> DB에서 검증한다는 것 자체가, Stateless하지 않은 느낌?

---

JWT 토큰으로 인증하면, userId 있을 텐데, 굳이 URL로 userId 세팅해줘야 할까?

> 세팅한다.
> 
> 세팅해서, 토큰의 userId와 URL의 userId 비교해서 맞는지 확인한다.  
> -> 확인할 때 latency가 발생하지만, JPA 1차캐시 덕분에 괜찮다고 생각함

---

메일 인증을 어떤 과정으로 해야 하나

메일 인증 요청
1. 메일이 유니크한지 체크
2. 인증번호 생성해서 메일 보냄 & 해당 인증번호 DB에 저장(DB에 id, 이메일, 인증번호 저장)

인증번호 체크
1. 요청받은 인증번호와 DB의 인증번호 비교함(이메일로 체크함)

---

프록시 객체 내부 호출 문제를 어떻게 해결 해야 하나

스프링의 AOP 프록시는 메서드 별로 프록시가 생성되지 않기 때문에, 메서드 내부에서 내부 메서드를 호출하면, 해당 메서드에는 프록시가 적용되지 않는다.

> 별도의 클래스로 분리해서 해결할 수 있지만, 내부 메서드의 코드 양이 얼마 안되므로, 
> 메서드 1개에 모든 코드를 넣는 것으로 결정

---

이메일 인증과 회원가입/비밀번호변경 기능을 메서드 1개에서 처리하는게 맞을까?

같이 처리하면
1. 클라이언트는 request를 1개만 보내도 됨 -> 불필요한 connection 감소
2. 하나의 메서드가 두가지 기능을 함 - 객체 지향적에서 별로임

따로 처리하면
1. 각각 다른 메서드로 처리하기 때문에 객체 지향적
2. 클라이언트가 request를 두 번 보내야 함

> 메서드 1개에서 같이 처리하는 것으로 결정
> 
> 아무리 생각해도 request를 두 번이나 보내는 건, 너무 불필요하다 생각

---

테스트 할 때도 SecurityConfig를 Import 해줘야 하는데,  
이를 import 하면, 관련 빈들을 주입 시켜야 한다 -> 테스트할 때는 필요없음

> @TestConfiguration 활용
> 
> 테스트와 실제 로직의 Config를 분리할 수 있다

---

static method는 어떻게 mocking?

---

Service 테스트할 때, 인증되어 있는지 확인 해야 하나?

> 안하는 것이 좋을 것 같다.
> 
> 실제 request에서 서비스단까지 갔다는 것은, 이미 인증이 되어 있다는 뜻  
> -> 불필요한 것같다.

---

테스트할 때 인증(Authentication) 된 객체를 어떻게 알 수 있을까

> 1. @WithMockUser  
> 지정된 사용자 정보로 UserDetails를 생성한 후 SecurityContext를 로드  
> (username: user, password: password, role: ROLE_USER)
> 2. @WithAnonymousUserUser  
> 익명의 사용자를 테스트하고 싶을 때 사용
> 3. @WithUserDetails  
> 내가 원하는 유저를 넣어서 테스트할 수 있다.  
> (대신 @BeforeTransaction으로 미리 유저를 넣어줘야 함)
> 4. 직접 SecurityContext에 인증된 객체를 넣어준다.  
> -> 이게 가장 간단할 듯  
> (컨트롤러에는 간단한 객체만 넣어서 validation만 검증하고, 서비스에서는 userId만 보내면 됨)

---

API 테스트가 필요하다.

> @SpringBootTest + @AutoConfigureMockMvc 이용한다  
> MockMvc를 이용해 request를 보내고, 모든 빈들이 등록됐기 때문에, 모든 빈들을 테스트할 수 있다.

---

HttpMessageNotReadableException: JSON parse error

> Dto에 @NoArgsConstructor가 필요하다. (not @AllArgsConstructor, @RequiredArgsConstructor)  
> @RequestBody는 기본 생성자를 이용하기 때문이다.

---

"java.util.LinkedHashMap cannot be cast to Class" 문제

다중 json 객체를 변환할 때 일어 나는 문제

```java
class TestObject{
    int age;
    String name;
    Friend friend;
}
```

위 객체는 Friend라는 객체를 포함하고 있기 때문에, 처음 JSON을 파싱했을 때는 문제가 없지만, Friend를 얻으려고 할 때 문제가 발생한다.

> 해결 방법
> 
> 처음 TestObject는 ObjectMapper로 그냥 읽고,  
> Friend 객체를 ObjectMapper를 통해 String으로 변환 후, 다시 읽는다.
> 
```java
TestObject testObject = objectMapper.readValue(jsonData, TestObject.class);
objectMapper.readValue(objectMapper.writeValueAsString(testObject.getFriend), Friend.class);
```

---

JWT 라이브러리는 액세스 토큰의 expiredTime의 밀리초가 0으로 세팅 된다.

> 테스트 할 때는, expiredTime을 일부러 (/1000 *1000)을 해줬음 - 밀리초 0으로 만들도록

---

