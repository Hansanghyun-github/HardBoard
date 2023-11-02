### 프로젝트 구조

api
- controller
  - ControllerAdvice.java
  - ApiResponse.java // ControllerAdvice에서 사용하는 response
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

