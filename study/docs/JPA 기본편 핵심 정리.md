# JPA 기본편 핵심 정리 — 전체 챕터 통합

> 김영한 「자바 ORM 표준 JPA 프로그래밍 - 기본편」 02~10장 학습 정리 통합본.
> 각 장의 상세 내용은 `study/docs/NN. 제목.md` 참고. 실습 환경: Java 17, Hibernate 6.4, H2, `jakarta.persistence.*`

---

## 1. JPA 구동 방식과 기본 규칙 (02장)

```
Persistence ──(설정 조회)──▶ META-INF/persistence.xml
     │ (생성)
     ▼
EntityManagerFactory (EMF) ──(생성)──▶ EntityManager (EM) × N
```

| 대상 | 규칙 | 이유 |
|------|------|------|
| **EntityManagerFactory** | 애플리케이션 전체에서 **딱 하나만** 생성해 공유 | 생성 비용이 크다 (커넥션풀 등) |
| **EntityManager** | **쓰레드 간 공유 X**, 한 번 쓰고 버린다 | DB 커넥션과 밀접, 공유 시 동시성 문제 |
| **데이터 변경** | 반드시 **트랜잭션 안에서** 실행 | 정합성 보장 |

```java
EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
EntityManager em = emf.createEntityManager();
EntityTransaction tx = em.getTransaction();
tx.begin();
try {
    // ... 데이터 작업 ...
    tx.commit();
} catch (Exception e) {
    tx.rollback();
} finally {
    em.close();    // 자원 해제는 생성의 역순 (EM → EMF)
    emf.close();
}
```

- **방언(Dialect)**: DB마다 다른 SQL 문법(VARCHAR/VARCHAR2, LIMIT/ROWNUM 등)을 JPA가 대신 처리.
  `hibernate.dialect` 로 지정하면 **DB를 바꿔도 방언만 교체**하면 된다.
- persistence.xml 속성 접두사: `jakarta.persistence.*` = JPA 표준, `hibernate.*` = 하이버네이트 전용.
  (강의 구버전은 `javax.persistence`)

---

## 2. 영속성 컨텍스트 (03장) — JPA에서 가장 중요한 개념

> JPA의 2대 핵심 = ① 객체와 관계형 DB 매핑(ORM) ② **영속성 컨텍스트**

**"엔티티를 영구 저장하는 환경"**. 논리적 개념이며 `EntityManager` 를 통해 접근한다.
`em.persist(entity)` 는 DB 저장이 아니라 **영속성 컨텍스트에 저장**하는 것이다.

### 엔티티 생명주기

| 상태 | 설명 | 관련 메서드 |
|------|------|------------|
| **비영속** (new/transient) | 영속성 컨텍스트와 무관한 순수 객체 | `new Member()` |
| **영속** (managed) | 영속성 컨텍스트가 관리 | `em.persist()`, `em.find()` |
| **준영속** (detached) | 영속이었다가 분리됨 — 컨텍스트 기능 전부 상실 | `em.detach()`, `em.clear()`, `em.close()` |
| **삭제** (removed) | 삭제 예정 | `em.remove()` |

### 영속성 컨텍스트의 5가지 이점

1. **1차 캐시** — `em.find()` 는 캐시 먼저 조회. 있으면 SQL 없이 반환, 없으면 DB 조회 후 캐시 저장.
2. **동일성(identity) 보장** — 같은 트랜잭션에서 같은 PK 조회 시 항상 같은 인스턴스 (`==` true).
   애플리케이션 차원의 REPEATABLE READ.
3. **쓰기 지연** — `persist()` 시 INSERT를 **쓰기 지연 SQL 저장소**에 쌓고, **커밋(flush) 때 한 번에** 전송.
4. **변경 감지 (Dirty Checking)** — 영속 엔티티는 **값만 바꾸면 UPDATE 자동**. `em.update()` 는 없다.
   원리: 최초 조회 시점 **스냅샷**을 떠두고, flush 때 [현재값 vs 스냅샷] 비교 → 다르면 UPDATE 생성.
5. **지연 로딩** — 연관 엔티티를 실제 사용 시점에 조회 (→ 프록시, 8장).

### 플러시 (flush)

**영속성 컨텍스트의 변경 내용을 DB에 반영(동기화)** 하는 것. **컨텍스트를 비우지 않는다.**

| 발생 시점 | 비고 |
|-----------|------|
| `em.flush()` 직접 호출 | 커밋 전에 SQL 로그를 미리 볼 수 있음 |
| 트랜잭션 커밋 | 자동 호출 |
| **JPQL 쿼리 실행** | 자동 호출 — persist만 된 엔티티를 JPQL이 조회 못 하면 곤란하므로 |

- `flush` = DB 동기화, `commit` = 트랜잭션 확정. flush 후 롤백하면 DB에 최종 반영되지 않는다.

---

## 3. 엔티티 매핑 (04장)

### @Entity / @Table

- `@Entity` 가 붙은 클래스는 JPA가 관리. 기본적으로 클래스 이름 = 테이블 이름.
- ⚠️ **기본 생성자 필수** (public/protected) — JPA가 리플렉션으로 객체·프록시를 만들 때 필요.
- final 클래스, enum, interface, inner 클래스 사용 불가. 저장 필드에 final 불가.
- `@Table(name = "MBR")` 로 테이블명 지정 가능.

### 스키마 자동 생성 — hibernate.hbm2ddl.auto

| 옵션 | 설명 |
|------|------|
| `create` | DROP + CREATE |
| `create-drop` | create + 종료 시 DROP |
| `update` | 변경분만 반영 (추가만 됨, 삭제 X) |
| `validate` | 매핑 정상 여부만 확인 |
| `none` | 미사용 |

⚠️ **운영 장비에는 절대 create/create-drop/update 금지.** 로컬은 create/update, 테스트는 update/validate, 운영은 validate/none.
DDL 제약조건(`nullable`, `length` 등)은 **DDL 생성에만 영향**, JPA 실행 로직과 무관.

### 필드-컬럼 매핑

| 어노테이션 | 설명 |
|-----------|------|
| `@Column` | 컬럼 매핑 (`name = "..."` 필수 형태, 기본 속성 없음) |
| `@Enumerated` | enum 매핑 — ⚠️ **반드시 `EnumType.STRING`**. ORDINAL은 enum 순서 변경 시 기존 데이터 의미가 전부 꼬이는 대형 사고 |
| `@Temporal` | `Date`/`Calendar` 용 — **`LocalDate`/`LocalDateTime` 쓰면 생략 가능** (자동 매핑) |
| `@Lob` | 문자면 CLOB, 나머지는 BLOB |
| `@Transient` | 매핑 제외, 메모리 전용 |

### 기본 키 매핑 — @Id / @GeneratedValue

| 전략 | 방식 | 특징 |
|------|------|------|
| `IDENTITY` | DB에 위임 (AUTO_INCREMENT) | ⚠️ id를 알려면 INSERT가 필요 → **persist 즉시 INSERT (쓰기 지연 X)** |
| `SEQUENCE` | DB 시퀀스 사용 | persist 시 **채번만** 하고 INSERT는 커밋까지 지연 (쓰기 지연 O). `allocationSize = 50` 으로 시퀀스 호출을 1/50로 줄이는 최적화 |
| `TABLE` | 키 생성 전용 테이블 | 모든 DB에서 동작하지만 성능 문제로 잘 안 씀 |
| `AUTO` | 방언에 따라 자동 선택 | 기본값 |

✅ **권장: `Long` 타입 + 대체 키 + 키 생성 전략.** 주민번호 같은 자연 키는 기본 키로 쓰지 말 것 (비즈니스 규칙은 바뀐다).
프리미티브 `long` 은 null이 없어 "아직 id 없는 비영속 상태"를 표현 못 하므로 래퍼 `Long` 사용.

---

## 4. 연관관계 매핑 (05~06장)

### 문제의식: 객체와 테이블의 패러다임 차이

- **테이블**은 **외래 키로 조인**, **객체**는 **참조**로 연관을 찾는다.
- FK 값을 `Long teamId` 필드로 들고 있으면 `order.getMember()` 같은 **객체 그래프 탐색이 불가능**.
- 해결: 참조 + 매핑 어노테이션.

```java
@ManyToOne(fetch = FetchType.LAZY)   // 다중성: 나(N) : 상대(1)
@JoinColumn(name = "TEAM_ID")        // 매핑할 FK 컬럼
private Team team;                   // FK 값이 아닌 참조
```

### 연관관계의 주인 (⭐ 최중요)

객체의 양방향 = 사실 **단방향 참조 2개**. 테이블의 FK는 **1개**. 그래서 누가 FK를 관리할지 정해야 한다.

- 두 관계 중 하나를 **주인(Owner)** 으로 지정 — **주인만 FK 등록/수정**, 반대편은 읽기 전용.
- 주인이 아닌 쪽에 `mappedBy = "주인의 필드명"` (컬럼명 아님!).
- ✅ **외래 키가 있는 곳(N쪽, `@ManyToOne` 쪽)을 주인으로.** 비즈니스 중요도 기준 금지.

```java
// 주인 (Member.team) — FK 관리
@ManyToOne @JoinColumn(name = "TEAM_ID")
private Team team;

// 반대편 (Team.members) — 읽기 전용
@OneToMany(mappedBy = "team")
private List<Member> members = new ArrayList<>();
```

### 양방향 매핑 주의사항

- **가장 흔한 실수**: 주인이 아닌 쪽(`team.getMembers().add(member)`)에만 값 설정 → **FK가 null로 저장**.
- 순수 객체 상태까지 고려해 **양쪽 다 설정** — 연관관계 편의 메서드로 실수 방지:

```java
public void changeTeam(Team team) {
    this.team = team;
    team.getMembers().add(this);
}
```

- `toString()`/JSON 직렬화가 양쪽을 서로 호출하면 **무한 루프** → 엔티티를 컨트롤러에서 직접 반환하지 말고 DTO로.
- **단방향만으로 매핑은 완료.** 양방향은 역방향 탐색이 필요할 때 추가 (테이블 영향 없음).

### 다중성별 선택 기준

| 다중성 | 판단 |
|--------|------|
| **다대일 (N:1)** | 기본형. FK 있는 N쪽에 `@ManyToOne` = 주인. 가장 많이 쓴다 |
| **일대다 (1:N) 단방향** | 가능하지만 FK가 반대편 테이블에 있어 **INSERT 후 UPDATE 추가 발생** → **다대일 양방향을 우선** |
| **일대일 (1:1)** | FK 위치 선택 가능 (주 테이블 FK = 매핑 편리·권장 / 대상 테이블 FK = 단방향 미지원, 양방향 필요). FK에 유니크 제약 |
| **다대다 (N:M)** | `@ManyToMany` 는 실무 금지 — 연결 테이블에 추가 컬럼(수량, 시간 등)을 못 넣는다 → **연결 엔티티로 승격** (`OrderItem`, `MemberProduct`) |

---

## 5. 고급 매핑 — 상속과 @MappedSuperclass (07장)

### 상속관계 매핑

RDB에는 상속이 없다 → 슈퍼타입-서브타입 모델로 매핑. **전략은 부모의 어노테이션 한 줄로 교체 가능, 객체 코드는 그대로.**

```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)  // 기본값은 SINGLE_TABLE
@DiscriminatorColumn(name = "DTYPE")             // 자식 구분 컬럼
public abstract class Item { ... }

@Entity
@DiscriminatorValue("A")
public class Album extends Item { ... }
```

| 전략 | 구조 | 장점 | 단점 |
|------|------|------|------|
| **JOINED** (기본 선택) | 부모+자식 각각 테이블, 자식 PK = 부모 FK | 정규화, FK 무결성, 저장 효율 | 조회 조인, INSERT 2번 |
| **SINGLE_TABLE** (단순할 때) | 한 테이블 + DTYPE | 조인 없음, 조회 단순·빠름 | 자식 컬럼 전부 **null 허용**, 테이블 비대 |
| **TABLE_PER_CLASS** (금지) | 자식 테이블마다 부모 컬럼 복사 | — | 부모 타입 조회 시 **UNION** — DB/ORM 전문가 모두 비추천 |

### @MappedSuperclass — 상속 매핑과 전혀 다른 용도

- 테이블과 매핑되지 않고 **자식에게 매핑 정보만 물려준다** (등록일/수정일 같은 공통 필드).
- 엔티티가 아니다 → `em.find(BaseEntity.class, ...)` 불가. **추상 클래스 권장**.

```java
@MappedSuperclass
public abstract class BaseEntity {
    private String createdBy;
    private LocalDateTime createdDate;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;
}
```

---

## 6. 프록시와 지연 로딩 (08장)

### 프록시의 구조

- `em.find()` = 즉시 DB 조회 / `em.getReference()` = **프록시** 반환 (SELECT 없음).
- 프록시 = 실제 엔티티를 **상속**한 가짜 객체. 내부에 id와 **target 참조**(처음엔 null)를 보관.
- id 외의 값을 처음 사용하는 순간 → 영속성 컨텍스트에 **초기화 요청** → DB 조회 → 진짜 엔티티 생성 → target 연결 → 이후 모든 호출은 **위임(delegate)**.
- ⚠️ 초기화돼도 **프록시가 진짜 엔티티로 바뀌는 게 아니다.** 계속 프록시인 채로 target에 위임.

### 프록시 특징 (시험 포인트)

- 타입 비교는 `==` 대신 **`instanceof`** (프록시는 상속받은 자식 클래스라 클래스가 다름).
- **1차 캐시에는 같은 id로 하나만** — 먼저 등록된 쪽이 주인:
  find 먼저 → getReference도 실제 엔티티 / getReference 먼저 → find도 프록시.
  이유: 같은 영속성 컨텍스트 안에서 `==` **동일성 보장** 때문.
- 준영속 상태(detach/clear)에서 초기화하면 **`LazyInitializationException`** — 실무 단골 예외.
  (최신 Hibernate는 `em.close()` 만으로는 재현 안 됨 — 트랜잭션 종료까지 자원 해제 지연)
- FK가 null이면 **프록시조차 안 만들어진다** (id 없이 프록시 생성 불가) → 필드에 그냥 null.
  `member.getTeam().getName()` 체이닝하면 NPE — null 체크 필요.

### 즉시 로딩 vs 지연 로딩

| 전략 | 동작 | 연관 필드 |
|------|------|-----------|
| `FetchType.LAZY` | 실제 사용할 때 별도 SELECT | 프록시 |
| `FetchType.EAGER` | 조회 시 조인해서 즉시 함께 | 실제 엔티티 |

⚠️ **기본값 함정**: `@ManyToOne`, `@OneToOne` = **EAGER** / `@OneToMany`, `@ManyToMany` = LAZY.

✅ **실무 지침: 모든 연관관계를 LAZY로 명시.** EAGER는 예상 못 한 조인과 **N+1 문제**의 주범.
- N+1: `select m from Member m` → SQL은 Member만 조회 → EAGER가 Team을 채우려고 **회원 수(N)만큼 추가 쿼리**.
- 함께 조회가 필요한 화면은 **JPQL fetch join** 으로 해결.

### 영속성 전이(CASCADE)와 고아 객체

```java
@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Child> childList = new ArrayList<>();
```

- **cascade** = 부모의 persist/remove 를 자식에게 **전파**하는 편의 기능. 연관관계 매핑(주인)과 무관.
- **orphanRemoval** = 부모와 연관이 **끊어진 자식을 자동 DELETE** (컬렉션에서 remove만 해도 삭제).

| 항목 | `cascade = REMOVE` | `orphanRemoval = true` |
|------|--------------------|------------------------|
| 부모 삭제 시 자식 삭제 | O | O |
| **컬렉션에서 제거 시** 자식 삭제 | **X** | **O** |

- 둘 다 **"참조하는 곳이 하나뿐 + 단일 소유"** 일 때만 사용 (예: Order → Delivery/OrderItem).
- `ALL + orphanRemoval = true` 조합 = 부모가 자식 생명주기를 완전 관리 (DDD Aggregate Root).

---

## 7. 값 타입 (09장)

### 분류

| 분류 | 식별자 | 생명주기 |
|------|--------|----------|
| **엔티티 타입** (`@Entity`) | O — 변해도 추적 가능 | 스스로 관리 |
| **값 타입** (기본값/임베디드/컬렉션) | X — 변경 시 추적 불가 | **엔티티에 의존** |

### 임베디드 타입 (@Embeddable / @Embedded)

```java
@Embeddable
public class Address {
    private String city;
    private String street;
    private String zipcode;
    // 기본 생성자 필수 + 생성자로만 값 설정 (Setter 금지 = 불변)
}

@Entity
public class Member {
    @Embedded
    private Address homeAddress;
}
```

- 별도 테이블이 아니라 **주인 테이블의 컬럼**으로 들어간다. 매핑 테이블은 사용 전후 동일.
- 재사용·응집도 향상 + `isWork()` 같은 의미 있는 메서드 보유 가능.
- 같은 값 타입을 한 엔티티에서 두 번 쓰면 `@AttributeOverrides` 로 컬럼명 재정의.

### ⚠️ 공유 참조와 불변 객체 (핵심)

```java
Address address = new Address("city", ...);
member1.setHomeAddress(address);
member2.setHomeAddress(address);          // 같은 인스턴스 공유!
member1.getHomeAddress().setCity("new");  // member2도 바뀜 → UPDATE 2방
```

- 섞이는 주체는 영속성 컨텍스트가 아니라 **자바 객체 참조**. 컴파일 시점에 못 잡는 부작용(side effect) 버그.
- ✅ 근본 해결: **값 타입은 불변 객체로** — 생성자로만 설정, Setter 없음. 수정은 **새 객체로 통째 교체**.
  > "불변이라는 작은 제약으로 부작용이라는 큰 재앙을 막을 수 있다."

### 값 타입 비교

- `==` (동일성, 참조 비교)이 아니라 **`equals()` (동등성, 값 비교)**.
- `equals()`/`hashCode()` **재정의 필수** — 재정의 안 하면 기본 구현이 `==` 과 동일.
- 컬렉션의 `remove(Object)` 도 equals 기반 → 미구현/값 불일치 시 **예외 없이 조용히 실패** (반환값 확인 습관!).

### 값 타입 컬렉션 (@ElementCollection)

```java
@ElementCollection
@CollectionTable(name = "FAVORITE_FOOD", joinColumns = @JoinColumn(name = "MEMBER_ID"))
@Column(name = "FOOD_NAME")
private Set<String> favoriteFoods = new HashSet<>();
```

- 컬렉션은 테이블에 못 넣으므로 **별도 테이블** + 모든 컬럼이 PK. 기본 **지연 로딩**.
- Cascade + 고아 제거를 **기본 내장**.
- ⚠️ `List` + 임베디드는 변경 시 **전체 DELETE 후 재INSERT** (행 특정 불가 — 식별자가 없으니까).
- ✅ 추적/변경이 필요하면 **엔티티로 승격** (id 부여 + `@OneToMany` + Cascade + orphanRemoval).
  **"식별자가 필요하면 그것은 값 타입이 아니라 엔티티다."**

---

## 8. JPQL — 객체지향 쿼리 언어 (10장)

### 쿼리 방법 선택

| 방법 | 평가 |
|------|------|
| **JPQL** | 기본기. 엔티티 객체 대상, SQL로 번역됨 |
| Criteria | 너무 복잡 → 비권장 |
| **QueryDSL** | 컴파일 시점 오류 검출, 동적 쿼리 → **실무 권장** |
| 네이티브 SQL / MyBatis / JdbcTemplate | DB 종속 기능 필요 시. ⚠️ JPA 우회 시 수동 flush 필요 |

### 기본 문법

- `select m from Member as m where m.age > 18` — **엔티티 이름** 사용, **별칭 필수**.
- 엔티티·속성은 대소문자 구분 O, JPQL 키워드는 구분 X.
- **파라미터는 이름 기준** 바인딩 (`:username`) — 위치 기준(`?1`)은 순서 밀리면 장애.
- `getResultList()` = 없으면 빈 리스트 (안전) / `getSingleResult()` = 없으면 `NoResultException`, 둘 이상이면 `NonUniqueResultException`.
- 프로젝션: 엔티티/임베디드/스칼라. 여러 값은 **`new` 명령어로 DTO 조회가 실무 권장** (`select new 패키지.DTO(m.username, m.age) ...`).
- 페이징: **`setFirstResult`(0부터) + `setMaxResults`** 끝. 방언이 DB별 SQL(LIMIT/ROWNUM) 처리.
- 조인: 내부/외부/세타 + `ON` 절 (조인 대상 필터링, 연관관계 없는 엔티티 외부 조인).
- 서브 쿼리: WHERE/HAVING (표준), SELECT (하이버네이트), FROM (하이버네이트 6+).
- ENUM 표현은 패키지명 포함 (`where m.type = jpql.MemberType.ADMIN`), CASE/COALESCE/NULLIF 지원.

### 경로 표현식 — 묵시적 조인 금지

| 종류 | 예 | 묵시적 조인 | 추가 탐색 |
|------|-----|------------|----------|
| 상태 필드 | `m.username` | X | X (탐색의 끝) |
| 단일 값 연관 필드 | `m.team` | ⚠️ 내부 조인 발생 | O |
| 컬렉션 값 연관 필드 | `t.members` | ⚠️ 내부 조인 발생 | X (별칭 얻어야 가능) |

✅ **묵시적 조인 대신 명시적 조인(`join`)** — 조인은 SQL 튜닝 포인트인데 묵시적 조인은 파악이 어렵다.

### 페치 조인 (⭐ 실무 최중요)

```sql
select m from Member m join fetch m.team          -- N:1
select t from Team t join fetch t.members         -- 1:N (컬렉션)
```

- SQL 조인 종류가 아니라 JPQL의 **성능 최적화 기능**. 연관 엔티티를 **SQL 한 번에 함께 조회** → **N+1 해결**.
- 글로벌 로딩 전략(LAZY)보다 **우선**한다. 페치 조인으로 가져오면 프록시가 아닌 **진짜 엔티티**가 들어온다.
- 1:N 조인은 DB row가 뻥튀기된다 (팀A에 회원 2명 → 팀A 2줄).

**한계 3가지**
1. **페치 조인 대상에 별칭 금지** — 객체 그래프는 "전부 조회"가 전제. 걸러서 가져오면 불완전한 컬렉션이 영속화되어 cascade/변경 감지가 오작동. 필터링이 필요하면 대상 엔티티로 따로 쿼리.
2. **둘 이상의 컬렉션** 페치 조인 불가.
3. **컬렉션 페치 조인 + 페이징 API 불가** — 경고만 남기고 **메모리에서 페이징** (매우 위험). 단일 값 연관은 페이징 가능.

**컬렉션 + 페이징의 해법 = `@BatchSize`** (또는 글로벌 `hibernate.default_batch_fetch_size`)

```java
@OneToMany(mappedBy = "team")
@BatchSize(size = 100)                 // ⚠️ 컬렉션 "필드"에! (클래스에 붙이면 프록시 배치)
private List<Member> members;
// → N+1 이 "팀 조회 1번 + where TEAM_ID in (?, ?, ...) 1번" 으로
```

✅ **실무 전략**: 전부 LAZY → 필요한 곳만 페치 조인 → 컬렉션+페이징은 BatchSize → 화면용 데이터는 DTO 조회.

### JPQL과 영속성 컨텍스트

| 조회 방식 | SQL 생략 가능? | 1차 캐시 역할 |
|-----------|---------------|--------------|
| `em.find()` | ✅ 캐시에 있으면 생략 | 조회 전 먼저 확인 |
| JPQL 엔티티 조회 | ❌ 항상 SQL 발사 | **결과 조립 시** 같은 식별자면 기존 엔티티 반환 (동일성 보장) |
| JPQL 스칼라 조회 | ❌ 항상 SQL 발사 | 관여 안 함 |

- JPQL은 어떤 조건이 올지 모르므로 1차 캐시로 답을 만들 수 없다 — 그래서 항상 DB로.
- 같은 이유로 JPQL 직전에 **자동 flush**.

### 기타 문법

- **엔티티 직접 사용**: JPQL에서 엔티티는 SQL에서 **기본 키 값으로 치환**.
  `where m = :member`(엔티티 바인딩) = `where m.id = :memberId`(Long 바인딩) — 같은 SQL.
  ⚠️ 섞으면 타입 불일치 예외 (`m = :param` 에 Long 넣기 금지).
- **Named 쿼리**: 정적 쿼리에 이름 부여. **로딩 시점에 문법 검증** + 파싱 캐시. XML(`<mapping-file>`, properties보다 앞에 선언)이 어노테이션보다 우선.
- **벌크 연산**: 쿼리 한 번으로 여러 row UPDATE/DELETE. 타입 없이 `createQuery(jpql)` + **`executeUpdate()`** (반환 = WHERE에 걸린 row 수).
  ⚠️ **영속성 컨텍스트를 무시하고 DB에 직접** → 벌크 연산을 먼저 실행하거나, 실행 후 **`em.clear()`**.

---

## 9. 강의(구버전)와 Hibernate 6 환경의 차이 모음

| 항목 | 강의 (Hibernate 5 / javax) | 내 환경 (Hibernate 6.4 / jakarta) |
|------|---------------------------|-----------------------------------|
| JPA 패키지 | `javax.persistence.*` | `jakarta.persistence.*` |
| 프록시 클래스명 | `...javassist...` | ByteBuddy 기반 `...HibernateProxy...` |
| `em.close()` 후 프록시 초기화 | 즉시 `LazyInitializationException` | 예외 안 남 (자원 해제 지연) → `detach`/`clear` 로 재현 |
| 임베디드 타입 안의 엔티티 필드 | 느슨하게 통과 | 매핑 명시 없으면 `Could not determine recommended JdbcType` |
| 날짜 매핑 | `@Temporal` 필요 | `LocalDate`/`LocalDateTime` 자동 매핑 |
| FROM 절 서브쿼리 | 미지원 | 지원 |
| 컬렉션 페치 조인 중복 | `distinct` 필요 | **자동 중복 제거** |
| `@BatchSize` IN 절 | 하위 배치 분할 (100→50→25...) | **size만큼 물음표 패딩** (실행 계획 재사용) |
| 벌크 UPDATE 타입 검사 | `p.price * 1.1` 통과 | Double→Integer 거부 → **`cast(... as Integer)` 필요** |
| 사용자 정의 함수 등록 | 생성자에서 `registerFunction()` | `initializeFunctionRegistry()` 오버라이드 + FunctionRegistry |

---

## ✅ 전체를 관통하는 원칙 10줄

1. **EMF는 하나, EM은 쓰고 버리고, 변경은 트랜잭션 안에서.**
2. 영속성 컨텍스트의 핵심 = **1차 캐시, 동일성 보장, 쓰기 지연, 변경 감지** — `em.update()` 는 없다.
3. 기본 키는 **`Long` + 대체 키 + 생성 전략**, enum은 **무조건 `STRING`**, 운영 DB에 ddl-auto 금지.
4. 연관관계의 주인 = **FK가 있는 N쪽**. 값은 주인에 설정하고, 편의 메서드로 양쪽을 맞춘다.
5. 다대일이 기본형, `@ManyToMany` 는 연결 엔티티로 승격, 상속은 JOINED 기본·TABLE_PER_CLASS 금지.
6. **모든 연관관계는 LAZY** — EAGER는 N+1의 주범. 함께 조회는 **페치 조인**으로.
7. cascade/orphanRemoval은 **단일 소유** 관계에만 — 부모가 자식 생명주기를 관리할 때.
8. **값 타입은 불변으로** 만들고 `equals()` 로 비교한다. 추적이 필요하면 엔티티로 승격.
9. JPQL은 **항상 SQL을 발사**하고 실행 직전 flush 된다. 벌크 연산 후에는 `em.clear()`.
10. 컬렉션 페치 조인에는 **페이징·별칭 금지** — 페이징이 필요하면 LAZY + `@BatchSize`.
