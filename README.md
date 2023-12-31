# 하드보드(HardBoard) 프로젝트

`하드보드 - 단단한 게시판`

## ✨ 프로젝트의 목적

기본적인 게시판 API를 성능 테스트를 통해 최적화 해보는 것

---
## ✔ 주요 기능

- 로그인
- 게시글 관리
- 댓글 관리
- 게시글과 댓글에 대한 추천/비추천
- 관리자의 공지사항 관리
- 유저의 문의
- 특정 유저 차단(차단하면 해당 유저의 글/댓글 보이지 않는다)
- 특정 글/댓글 신고

---
## 🛠️개발 언어 및 활용 기술

**개발 환경**

- **Springboot**를 이용해서 웹 애플리케이션 서버를 구축했습니다.
- 빌드도구로 **Gradle**을 사용했습니다
- **Spring Data JPA(Hibernate로 구현)** 와 **QueryDSL** 로 DB의 데이터를 관리했습니다.

**시큐리티**

- **Spring Security** 를 사용했습니다.
- 스프링 시큐리티의 세션방식이 아닌 **JWT** 토큰 방식을 이용해서 인증을 진행했습니다.
- JWT 액세스 토큰을 발급할때 액세스 토큰의 만료시간을 짧게 하고, 리프레시 토큰을 같이 발급하여 보안을 강화했습니다.

---
## 🏗️ERD 다이어그램
![ERD](https://github.com/Hansanghyun-github/HardBoard/assets/56988779/7383d70b-ea72-4925-8e7a-4b7f325f0587)

---
## 🏗️API 명세서
<a href="https://statuesque-step-7d4.notion.site/API-4adcc08f25d14ae2ba0c5fb12955929d" target="_blank">API 명세서</a>

