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

