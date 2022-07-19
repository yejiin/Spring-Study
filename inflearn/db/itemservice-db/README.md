## 데이터 접근 기술

### 1. JdbcTemplate

#### 기능 정리

- `JdbcTemplate` : 순서 기반 파라미터 바인딩 지원
- `NamedParameterJdbcTemplate` : 이름 기반 파라미터 바인등을 지원 (권장)
- `SimpleJdbcInsert` : Insert SQL 을 편리하게 사용
- `SimpleJdbcCall` : 스토어드 프로시저를 편리하게 호출할

> 개발을 할 때, 모호함을 제거해서 코드를 명확하게 만드는 것이 유지보수 관점에서 매우 중요

<br>

### 2. MyBatis

#### MyBatis가 제공하는 동적 SQL 기능

- `if`
- `choose (when, otherwise)`
- `trim (where, set)`
- `foreach`

#### 기타 기능

- 애노테이션으로 SQL 작성 
  - 동적 SQL 이 해결되지 않으므로 간단한 경우에만 사용한다.
- 문자열 대체 `${}`
  - 파라미터 바인딩이 아니라 문자 그대로를 처리
- 재사용 가능한 SQL 조각
- Result Maps 
  - 복잡한 결과 매핑



<br>

### 3. JPA

> **설정 (참고)**
>
> JPA는 `EntityManagerFactory`, JPA 트랜잭션 매니저(`JpaTransactionManager`), 데이터소스 등 다양한 설정 필요. 
> 스프링 부트는 이 과정을 모두 자동화 해줌(`JpaBaseConfiguration`) 

**JPQL (Java Persistence Query Language)**  - 객체지향 쿼리 언어 제공

#### JPA 예외 변환

- 순수한 JPA를 사용하면 `EntityManager` 는 `PersistenceException` 과 그 하위 예외 발생
- `@Repository` 가 붙은 클래스는 스프링 예외 변환을 처리하는 AOP의 적용 대상이 되어 스프링 데이터 접근 예외로 변환 (`스프링 예외 추상화`)



<br>

### 4. 스프링 데이터 JPA

> JPA를 편리하게 사용할 수 있도록 도와주는 라이브러리

#### 대표 기능

- 공통 인터페이스 기능 - `JpaRepository`
  - 스프링 데이터 JPA가 구현 클래스를 대신 생성
- 쿼리 메서드 기능
  - 메서드 이름을 분석해서 쿼리를 자동으로 생성 및 실행



<br>

### 5. Querydsl

- Querydsl을 사용하려면 `JPAQueryFactory` 필요, `JpaQueryFactory` 는 JPQL 을 만들기 때문에 `EntityManager` 필요

- **장점** : 동적 쿼리 사용, 컴파일 시점에 오류를 막을 수 있음

  