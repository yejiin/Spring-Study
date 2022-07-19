## 스프링 트랜잭션

### 스프링이 제공하는 트랜잭션 AOP (프록시 방식)

- `@Transactional` - 이 애노테이션을 인식해서 트랜잭션이 처리하는 프록시를 적용

<br>

### 트랜잭션 적용 위치
- 클래스에 적용하면 메서드는 자동 적용된다.
- 우선 순위 - 더 구체적이고 자세한 것이 높은 우선순위를 가진다.

  ```
  1. 클래스의 메서드 (가장 높은 우선순위)
  2. 클래스의 타입
  3. 인터페이스의 메서드
  4. 인터페이스의 타입 (가장 낮음)
  (인터페이스에 @Transactional 권장x)
  ```

<br>

### 트랜잭션 AOP 주의 사항 

- AOP를 적용하면 스프링은 대상 객체 대신에 프록시를 스프링 빈으로 등록
- public 메서드만 트랜잭션 적용
- 스프링 초기화 시점에 트랜잭션 AOP 가 적용되지 않을 수 있음
- 프록시 내부 호출 문제 

	![](https://user-images.githubusercontent.com/63090006/179696124-67e291a7-5d80-40bb-b767-2e2026daf4e4.png)
	1. `callService.external()` 호출. 
	2. `callService` 의 트랜잭션 프록시 호출.
	3. `external()` 메서드에는 `@Transactional` 이 없으므로 트랜잭션 적용 X
	4. 실제 `callService` 객체 인스턴스의  `external()` ghcnf
	5. `external()` 은 내부에서 `internal()` 메서드 호출 
	6. 내부 호출은 프록시를 거치지 않기 때문에 `internal()` 메서드에 트랜잭션 적용 X 

> => 내부 호출 문제를 해결하기 위해 `internal()` 메서드를 별도의 클래스로 분리 (다른 방법 사용 가능)

<br>

### 트랜잭션 옵션

#### value, transactionManager

- 트랜잭션 프록시가 사용할 트랜잭션 매니저 지정
- 생략 가능
- `@Transactional("txManager")`


#### rollbackFor

- 어떤 예외가 발생할 때에 따른 롤백 지정
- `@Transactional(rollbackFor = Exception.class)`
  - `Exception` 이 발생해도 롤백


#### noRollbackFor

- 어떤 예외가 발생하더라도 롤백하지 않도록 지정


#### progation

- 트랜잭션 전파


#### isolation

- 트랜잭션 격리 수준 지정
- `DEFAULT` 는 데이터베이스에서 설정한 격리 수준을 따름


#### timeout

- 트랜잭션 수행 시간에 대한 타임아웃을 초 단위로 지정
- `DEFAULT` 는 트랜잭셔 시스템의 타임아웃


#### label

- 트랜잭션 애노테이션에 있는 값을 직접 읽어서 어떤 동작을 하고 싶을 때 사용


#### readOnly

- `DEFAULT` 는 `readOnly = false`
- `readOnly = true` 옵션을 사용하면 읽기에서 다양한 성능 최적화가 발생할 수 있음

<br>

### Exception - 트랜잭션 commit | rollback

- 언체크 예외 (`RuntimeException`, `Error`) 
  - 복구 불가능한 예외
  - 하위 예외가 발생하면 트랜잭션 **롤백**
- 체크 예외 (`Exception`) 
  -  비즈니스 의미가 있을 때 사용
  - 하위 예외가 발생하면 트랜잭션 **커밋**

> => `rollbackFor` 옵션 사용해서 비즈니스 상황에 따라서 커밋과 롤백 선택

<br>

### 스프링 트랜잭션 전파 (`REQUIRED` 옵션 기준)

> **전제**
> 1번 트랜잭션이 수행중인데, 2번 트랜잭션이 추가로 수행됨
> => 1번 트랜잭션 (**외부 트랜잭션**), 2번 트랜잭션 (**내부 트랜잭션**)

![](https://user-images.githubusercontent.com/63090006/179697095-240922cf-d2af-4dbe-8c67-a4523ea6e3cc.png)
- 스프링의 경우 외부 트랜잭션과 내부 트랜잭션을 묶어서 하나의 트랜잭션으로 만들어줌

![](https://user-images.githubusercontent.com/63090006/179697173-923927f5-510a-4bac-bfa4-028537191587.png)
- 물리 트랜잭션 - 실제 데이터베이스에 적용되는 트랜잭션
- 논리 트랜잭션 - 트랜잭션 메니저를 통해 트랜잭션을 사용하는 단위

```
원칙
1. 모든 논리 트랜잭션이 커밋되어야 물리 트랜잭션이 커밋된다.
2. 하나의 논리 트랜잭션이라도 롤백되면 물리 트랜잭션을 롤백된다.	
```

	- 모든 논리 트랜잭션이 커밋되면 물리 트랜잭션도 커밋
	- 외부 논리 트랜잭션이 롤백되면 물리 트랜잭션도 롤백
	- 내부 논리 트랜잭셩이 롤백되면 물리 트랜잭션도 롤백
