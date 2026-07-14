# JPA-study

김영한 JPA 로드맵을 따라 직접 실습 코드를 작성하고, 챕터별로 학습 내용을 마크다운으로 정리하는 저장소.

- **1단계**: 자바 ORM 표준 JPA 프로그래밍 - 기본편 ✅ 완료
- **2단계**: 실전! 스프링 부트와 JPA 활용 1편 🚧 진행 중

## 학습 정리 진행 상황

### 실전! 스프링 부트와 JPA 활용 1편 (진행 중)

실습 코드: [`study/SpringBootJPA/jpashop`](study/SpringBootJPA/jpashop)

| # | 챕터 | 정리 |
|---|------|------|
| 01 | 프로젝트 환경설정 | [01. 프로젝트 환경설정.md](study/docs/SpringBootJPA/01.%20프로젝트%20환경설정.md) |
| 02 | 도메인 분석 설계 | [02. 도메인 분석 및 설계.md](study/docs/SpringBootJPA/02.%20도메인%20분석%20및%20설계.md) |
| 03 | 애플리케이션 구현 준비 | |
| 04 | 회원 도메인 개발 | [04. 회원 도메인 개발.md](study/docs/SpringBootJPA/04.%20회원%20도메인%20개발.md) |
| 05 | 상품 도메인 개발 | |
| 06 | 주문 도메인 개발 | |
| 07 | 웹 계층 개발 | |

### 자바 ORM 표준 JPA 프로그래밍 - 기본편 (완료)

📌 **[JPA 기본편 핵심 정리](study/docs/jpaBasic/JPA%20기본편%20핵심%20정리.md)** — 전 챕터 통합 요약
실습 코드: `study/jpaBasic`

<details>
<summary><b>챕터별 정리</b> (02~10장 완료)</summary>

| # | 챕터 | 정리 |
|---|------|------|
| 02 | JPA 시작 | [02. JPA 시작.md](study/docs/jpaBasic/02.%20JPA%20시작.md) |
| 03 | 영속성 관리 | [03. 영속성 관리.md](study/docs/jpaBasic/03.%20영속성%20관리.md) |
| 04 | 엔티티 매핑 | [04. 엔티티 매핑.md](study/docs/jpaBasic/04.%20엔티티%20매핑.md) |
| 05 | 연관관계 매핑 기초 | [05. 연관관계 매핑 기초.md](study/docs/jpaBasic/05.%20연관관계%20매핑%20기초.md) |
| 06 | 다양한 연관관계 매핑 | [06. 다양한 연관관계 매핑.md](study/docs/jpaBasic/06.%20다양한%20연관관계%20매핑.md) |
| 07 | 고급 매핑 | [07. 고급 매핑.md](study/docs/jpaBasic/07.%20고급%20매핑.md) |
| 08 | 프록시와 연관관계 관리 | [08. 프록시와 연관관계 관리.md](study/docs/jpaBasic/08.%20프록시와%20연관관계%20관리.md) |
| 09 | 값 타입 | [09. 값 타입.md](study/docs/jpaBasic/09.%20값%20타입.md) |
| 10.1 | 객체지향 쿼리 언어 — 기본 문법 | [10.1 객체지향 쿼리 언어.md](study/docs/jpaBasic/10.1%20객체지향%20쿼리%20언어.md) |
| 10.2 | 객체지향 쿼리 언어 — 중급 문법 | [10.2 객체지향 쿼리 언어.md](study/docs/jpaBasic/10.2%20객체지향%20쿼리%20언어.md) |

</details>

## 개발 환경

### 활용 1편 — `study/SpringBootJPA/jpashop`

| 항목 | 버전 |
|------|------|
| Spring Boot | 3.5.16 |
| Java | 17 |
| 빌드 | Gradle (Groovy) |
| DB | H2 2.x (`jdbc:h2:tcp://localhost/~/jpashop`) |
| 주요 의존성 | web, thymeleaf, data-jpa, h2, lombok, validation, p6spy |

### 기본편 — `study/jpaBasic`

| 항목 | 버전 |
|------|------|
| Java | 17 |
| Hibernate ORM | 6.4.2.Final |
| H2 Database | 2.2.224 |
| 빌드 | Maven |
| JPA 패키지 | `jakarta.persistence.*` (강의 구버전은 `javax.persistence.*`) |

## 참고

- 스프링 기초가 필요할 때: [spring-study/issues](https://github.com/titianfall/spring-study/tree/main/issues) — 빈/DI, 웹 MVC, DB 접근 기술, AOP
- Hibernate ↔ Spring Boot 버전 호환: Spring Boot BOM이 Hibernate 버전을 관리하므로, 부트 프로젝트에서는 버전 태그 없이 `spring-boot-starter-data-jpa`에 맡긴다.
