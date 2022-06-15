# JDBC 
## JDBC 표준 인터페이스
- `java.sql.Connection` - 연결
- `java.sql.Statement` - SQL을 담은 내용
- `java.sql.ResultSet` - SQL 요청 응답

![JDBC](https://user-images.githubusercontent.com/63090006/172969852-a6de8722-15ca-4f43-868d-556e8453543e.jpeg)

<br>

## DriverManager (V0)
- DB 드라이버들을 관리하고, 커넥션을 획득하는 기능 제공
- JDBC 라이브러리
### DriverManager 커넥션 요청 흐름
![DriverManager](https://user-images.githubusercontent.com/63090006/172969463-1b064329-e5be-4337-9553-c252321c95a7.jpeg)

<br>


## DataSource (V1)
- **커넥션을 획득하는 방법을 추상화**하는 인터페이스
![DataSource](https://user-images.githubusercontent.com/63090006/172969334-dccec898-1342-47be-ac33-0cf46d04b0bb.jpeg)

<br>

## 같은 커넥션을 유지하기 위해 커넥션을 파라미터로 전달 (V2)
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

<br>

## 위의 코드들의 문제점
1. 트랜잭션 문제
   - JDBC 구현 기술이 서비스 계층에 누수되는 문제
   - 트랜잭션 동기화 문제
   - 트랜잭션 적용 반복 문제 (`try`, `catch`, `finally`)
2. 예외 누수 문제
   - 데이터 접근 계층의 JDBC 구현 기술 예외가 서비스 계층으로 전파됨
3. JDBC 반복 문제
   - `try`, `catch`, `finally`...
   
### 해결방안
**트랜잭션 기능 추상화**
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


**리소스 동기화**
- **트랜잭션 매니저**(스프링은 트랜잭션 동기화 매니저를 제공)를 사용하여 해결 (**V3_1**)
![](https://user-images.githubusercontent.com/63090006/172992647-d0306615-11b3-441e-b1d5-ef193f39d770.jpeg)


**탬플릿 콜백 패턴**
- 트랜잭션의 적용 반복 문제 해결 (**V3_2**)

**스프링 AOP를 통해 프록시 도입**
- JDBC 구현 기술이 서비스 계층에 누수되는 문제 해결 
- @Transactional (**V3_3**)
- 데이터소스와 트랜잭션 매니져 자동 등록 (**V3_4**) 

![](https://user-images.githubusercontent.com/63090006/172992704-012497b6-1bbc-4160-8f4a-678a26e3949e.jpeg)