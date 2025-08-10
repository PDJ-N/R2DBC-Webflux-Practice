# 💡 Spring WebFlux + R2DBC + MySQL 연습 레포지토리

> Spring WebFlux에서 R2DBC를 이용해 MySQL과 논블로킹 방식으로 연동하는 연습용 저장소입니다.  
> 학습 목적이며, 실무 코드 품질이나 구조를 목표로 하진 않습니다.

---

## 🔧 사용 기술

- Java 17
- Spring Boot 3.x
- Spring WebFlux
- Spring Data R2DBC
- MySQL
- R2DBC MySQL Driver
- WebTestClient (통합 테스트용)
- Gradle

---

## 📌 주요 학습 내용

- R2DBC 설정 방법 (application.yml)
- Entity + Repository + Service + Controller 구성
- `Mono`, `Flux` 사용법
- WebTestClient를 이용한 간단한 통합 테스트
- Flyway를 사용한 데이터베이스 마이그레이션(버전 관리)
- WebFlux에서 Spring Security + JWT 구현을 통한 보안

---

## 🧪 API 테스트 방법

[Swagger 문서화 링크](http://localhost:8080/webjars/swagger-ui/index.html)

위 링크에 들어가서 만든 REST API 테스트가 가능하며 `src/test`에 있는 테스트 코드로도 코드 테스트가 가능함.



---

## 참고
* 실습을 목적으로 하기 때문에 구조나 예외 처리는 간단히 작성됨.
* 몇몇 API들은 유효한 값이 들어온다는 가정 하에 Validation을 최소화함.
    * ex: 이메일 정규식 검사, 비밀번호 정규식 검사 등 
* 필요한 기능은 추가 실습하면서 점진적으로 확장할 예정

---

## TODO

* [ ] `DatabaseClient` 사용해보기.
* [ ] R2DBC 트랜잭션 실습