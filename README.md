# Hibernate ↔ Spring Boot 버전 호환 정리

> `ex1-hello-jpa` 프로젝트를 나중에 Spring Boot로 옮길 때 참고용
> 확인일: 2026-07-01 (Spring Boot 공식 dependency-versions 문서 기준)

## 현재 pom.xml 의존성

| 항목 | 버전 |
|------|------|
| `org.hibernate:hibernate-core` | **6.4.2.Final** |
| Java | 17 |
| `com.h2database:h2` | 2.2.224 |
| `javax.xml.bind:jaxb-api` | 2.3.1 |

## 핵심 결론

⚠️ **Spring Boot 어떤 버전도 Hibernate `6.4.2.Final`을 정확히 관리(BOM 고정)하지 않는다.**
Spring Boot는 6.4.1 → (6.4.2 / 6.4.3 스킵) → 6.4.4 로 건너뛰었다.

따라서 6.4.x 라인에서 가장 가까운 선택지는 아래 둘.

| Spring Boot | 관리하는 Hibernate ORM | H2 | 비고 |
|-------------|----------------------|-----|------|
| **3.2.2** | 6.4.1.Final | 2.2.224 | 현재보다 patch 한 단계 **낮음** |
| **3.2.3** | 6.4.4.Final | 2.2.224 | 현재보다 patch 몇 단계 **높음** |

- H2 `2.2.224`는 Spring Boot 3.2.2 / 3.2.3 모두 동일하게 관리 → 현재 pom과 일치 ✅
- Java 17은 Spring Boot 3.x 최소 요구사항(17+)과 일치 ✅

## 권장 방향

### 1. 정확한 버전 매칭은 사실상 불필요
스터디 목적이면 Hibernate patch 버전 하나(6.4.1 vs 6.4.2 vs 6.4.4)는 기능 차이가 거의 없다.
**6.4.x 라인의 Spring Boot 3.2.x 아무거나** 쓰면 된다. (3.2.2 또는 3.2.3 권장)

### 2. Spring Boot로 옮길 때는 Hibernate 버전을 직접 명시하지 않는다
스타터가 알아서 호환 버전을 끌어온다. `hibernate-core`, `h2` 버전 태그를 **지워야** BOM이 관리한다.

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.3</version>   <!-- 또는 3.2.2 -->
    <relativePath/>
</parent>

<properties>
    <java.version>17</java.version>
</properties>

<dependencies>
    <!-- hibernate-core 6.4.x 를 자동으로 가져옴 (버전 명시 X) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- H2 (버전도 부트가 관리) -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

### 3. 학습 흐름 참고
- **JPA 기본편(이 프로젝트)** = 순수 JPA → Spring Boot 없이 진행하는 게 정석
- 다음 단계(스프링 데이터 JPA / 활용편)에서 Spring Boot 도입
- 그때는 "이 Hibernate와 똑같이"보다 **최신 안정 부트 버전** 사용이 일반적

## 출처
- [Spring Boot 3.2.2 Dependency Versions](https://docs.spring.io/spring-boot/docs/3.2.2/reference/html/dependency-versions.html) → Hibernate 6.4.1.Final
- [Spring Boot 3.2.3 Dependency Versions](https://docs.spring.io/spring-boot/docs/3.2.3/reference/html/dependency-versions.html) → Hibernate 6.4.4.Final
- [Spring Boot 3.2 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.2-Release-Notes)
