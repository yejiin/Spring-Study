### 목차
1. [JDBC 이해](#jdbc-이해)
2. [커넥션풀과 데이터소스 이해](#커넥션풀과-데이터소스-이해)
3. [트랜잭션 이해](#트랜잭션-이해)
4. [스프링과 문제 해결 - 트랜잭션](#스프링과-문제-해결---트랜잭션)
5. [자바 예외 이해](#자바-예외-이해)
6. [스프링과 문제 해결 - 예외 처리, 반복](#스프링과-문제-해결---예외-처리와-반복)

<br>
<br>


# JDBC 이해

**JDBC** : 자바에서 데이터베이스에 접속할 수 있도록 하는 자바 API

**JDBC 드라이버** : JDBC 인터페이스를 DB에 맞도록 구현한 라이브러리



## JDBC 표준 인터페이스
- `java.sql.Connection` - 연결
- `java.sql.Statement` - SQL을 담은 내용
- `java.sql.ResultSet` - SQL 요청 응답

![JDBC](https://user-images.githubusercontent.com/63090006/172969852-a6de8722-15ca-4f43-868d-556e8453543e.jpeg)



## JDBC 접근 기술

### SQL Mapper

- SQL 응답 결과를 객체로 편리하게 변환해준다.
- 개발자가 SQL을 직접 작성해야한다.
- Ex) 스프링 JdbcTemplate, MyBatis

### ORM

- 객체를 관계형 데이터베이스 테이블과 매핑해주는 기술이다.
- ORM 기술이 개발자 대신에 SQL을 동적으로 만들어 실행해준다.
- JPA는 자바 진영의 ORM 표준 인터페이스이고, 이것을 구현한 것으로 하이버네이트와 이클립스 링크 등의 구현 기술이 있다.
- Ex) JPA, 하이버네이트, 이클립스 링크
  

## DriverManager `V0`
- DB 드라이버들을 관리하고, 커넥션을 획득하는 기능 제공
- JDBC 라이브러리

### DriverManager 커넥션 요청 흐름
![DriverManager](https://user-images.githubusercontent.com/63090006/172969463-1b064329-e5be-4337-9553-c252321c95a7.jpeg)

<br>



# 커넥션풀과 데이터소스 이해

## 커넥션풀

	- 커넥션을 미리 생성해두고 사용하는 방법
	- 커넥션의 기본 개수는 10개
	- 커넥션 풀에 들어 있는 커넥션은 TCP/IP로 DB와 커넥션이 연결되어 있는 상태이기 때문에 언제든지 즉시 SQL을 DB에 전달
	- `commons-dbcp2`, `tomcat-jdbc pool`, `HikariCP`


## DataSource `V1`
- **커넥션을 획득하는 방법을 추상화**하는 인터페이스
![DataSource](https://user-images.githubusercontent.com/63090006/172969334-dccec898-1342-47be-ac33-0cf46d04b0bb.jpeg)

<br>



# 트랜잭션 이해

**Transaction** : 업무 처리의 최소 단위

**Commit** : 모든 작업이 성공해서 데이터베이스에 정상 반영하는 것

**Rollback** : 작업 중 하나라도 실패해서 거래 이전으로 되돌리는 것

### 트랜잭션 ACID
- `원자성`: 트랜잭션 내에서 실행한 작업들은 마치 하나의 작업인 것처럼 모두 성공하거나 모두 실패해야 한다.
- `일관성`: 모든 트랜잭션은 일관성 있는 데이터베이스 상태를 유지해야 한다.
- `격리성`: 동시에 실행되는 트랜잭션들이 서로에게 영향을 미치지 않도록 해야한다.격리성은 동시성과 관련된 성능 이슈로 인해 트랜잭션 격리 수준(Isolation level)을 선택할 수 있다.
- `지속성`: 트랜잭션을 성공적으로 끝내면 그 결과가 항상 기록되어야 한다. 중간에 시스템에 문제가 발생해도 데이터베이스 로그 등을 사용해서 성공한 트랜잭션 내용을 복구해야 한다.

### 트랜잭션 격리 수준 - Isolation level
- READ UNCOMMITTED(커밋되지 않은 읽기)
- READ COMMITTED(커밋된 읽기)
- REPEATABLE READ(반복 가능한 읽기)
- SERIALIZABLE(직렬화 가능)

> - 애플리케이션에서 DB 트랜잭션을 사용하려면 **트랜잭션을 사용하는 동안 같은 커넥션을 유지**헤야 함
> - 트랜잭션을 비즈니스 로직이 있는 서비스 계층에서 시작해야 함

## 같은 커넥션을 유지하기 위해 커넥션을 파라미터로 전달 	`V2`

<br>



# 스프링과 문제 해결 - 트랜잭션

## 애플리케이션 구조

1. 프레젠테이션 계층 `@Controller`

   - UI와 관련된 처리 담당
   - 웹 요청과 응답
   - 사용자 요청을 검증
   - 주 사용 기술: 서블릿과 HTTP 같은 웹 기술, 스프링 MVC

2. 서비스 계층 `@Service`

   - 비즈니스 로직을 담당
   - 주 사용 기술: 가급적 **특정 기술에 의존하지 않고**, **순수 자바 코드**로 작성

3. 데이터 접근 계층 `@Repository`

   - 실제 데이터베이스에 접근하는 코드
   - 주 사용 기술: JDBC, JPA, File, Redis, Mongo ...

   

## 위의 코드들의 문제점
1. 트랜잭션 문제
   - JDBC 구현 기술이 서비스 계층에 누수되는 문제
   - 트랜잭션 동기화 문제
   - 트랜잭션 적용 반복 문제 (`try`, `catch`, `finally`)
2. 예외 누수 문제
   - 데이터 접근 계층의 JDBC 구현 기술 예외가 서비스 계층으로 전파됨
3. JDBC 반복 문제
   - `try`, `catch`, `finally`...
   
## 해결방안

### 트랜잭션 매니저를 사용하여 해결 `V3_1`

**1. 트랜잭션 기능 추상화**

- JDBC 기술을 사용하다가 JPA 기술로 변경하게 되면 서비스 계층의 코드도 JPA 기술을 사용하도록 함께 수정해야 하는 문제 해결
- 트랙잭션 추상화 인터페이스
  ```java
  public interface TxManager {
    begin();
    commit();
    rollback();
  }
  ```
- 스프링의 트랜잭션 추상화
![Spring_Transaction_Manager](https://user-images.githubusercontent.com/63090006/172969779-d35633f3-ed11-479b-8f9c-bab3992b3562.jpeg)

**2. 리소스 동기화**

- 스프링은 트랜잭션 동기화 매니저를 제공
![](https://user-images.githubusercontent.com/63090006/172992647-d0306615-11b3-441e-b1d5-ef193f39d770.jpeg)

### 탬플릿 콜백 패턴 `V3_2`

- 트랜잭션의 적용 반복 문제 해결 (commit, rollback 코드 제거) `TransactionTemplate`

### 스프링 AOP를 통해 프록시 도입

- JDBC 구현 기술이 서비스 계층에 누수되는 문제 해결 
- @Transactional **`V3_3`**
- 데이터소스와 트랜잭션 매니져 자동 등록 **`V3_4`**

![](https://user-images.githubusercontent.com/63090006/172992704-012497b6-1bbc-4160-8f4a-678a26e3949e.jpeg)

<br>



# 자바 예외 이해

## 예외 계층

<img width="472" alt="image" src="https://user-images.githubusercontent.com/63090006/173957860-2e61bd4b-bb86-4ee4-a428-2418ab84cd2b.png">

- `Throwable` : 최상위 예외
- `Error` : 메모리 부족이나 심각한 시스템 오류와 같이 **애플리케이션에서 복구 불가능한 시스템 예외**
- `Exception` : 체크 예외
- `RuntimeException` : 언체크 예외, 런타임 예외
  - 컴파일러가 체크하지 않음



## 예외 기본 규칙

1. 예외 처리
2. 예외 던짐

> **참고** - 예외를 처리하지 못하고 계속 던지면 어떻게 될까?
>
> - 자바 `main()` 쓰레드의 경우, 예외 로그를 출력하면서 시스템이 종료된다.
> - 웹 애플리케이션의 경우, WAS가 해당 예외를 받아서 처리하는데, 주로 사용자에게 개발자가 지정한 페이지 또는 오류 페이지를 보여준다.



## 체크 예외

- 체크 예외는 잡아서 처리하거나, 또는 밖으로 던지도록 선언해야한다. 그렇지 않으면 컴파일 오류 발생
- 예외를 잡아서 처리할 수 없을때, 예외를 밖으로 던지는 `throws 예외`를 필수로 선언해야 함

> **참고** - 예외 스택 트레이스
>
> ```java
> log.info("예외 처리, message={}", e.getMessage(), e);
> ```
>
> 로그를 남길 때 로그의 마지막 인수에 예외 객체를 전달해주면 로그가 해당 예외의 스택 트레이스를 추가로 출력



## 언체크 예외

- 컴파일러가 예외를 체크하지 않음
- 예외를 던지는 `throws` 를 선언하지 않고 생략 가능



> **예외 기본 원칙**
>
> - 기본적으로 언체크(런타임) 예외를 사용하자
> - 체크 예외는 비즈니스 로직상 의도적으로 던지는 예외에만 사용하자
>   - 해당 예외를 잡아서 반드시 처리해야 하는 문제일 때만

<br>



# 스프링과 문제 해결 - 예외 처리와 반복

## 스프링 데이터 접근 예외 계층
<img width="515" alt="image" src="https://user-images.githubusercontent.com/63090006/173957690-56a87f5a-c22e-4957-8113-7b707b28077a.png">

- Transient : 일시적  
  - Ex) 쿼리 타임아웃, 락  
- NoTransient : 일시적이지 않음  
  - Ex) SQL 문법 오류, 데이터베이스 제약조건 위배


### 예외 처리 -> 스프링 예외 변환기 `SQLExceptionTranslator`
```java
  SQLExceptionTranslator exTranslator = new
  SQLErrorCodeSQLExceptionTranslator(dataSource);
  DataAccessException resultEx = exTranslator.translate("select", sql, e);
```

### JDBC 반복 -> JdbcTemplate
