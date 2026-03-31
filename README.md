# QooTalk Server

## 프로젝트 설명 및 목차

QooTalk Server는 사내 커뮤니케이션을 위한 채팅 백엔드 서버입니다.  
사용자 인증부터 채팅방 관리, 메시지 송수신, 읽음 처리, 파일 업로드까지 이어지는 흐름을 하나의 멀티모듈 Spring Boot 프로젝트로 구성했습니다.

- JWT 기반 인증 및 사용자 프로필 관리
- 채팅방 생성, 조회, 수정, 삭제
- 메시지 전송, 수정, 삭제, 읽음 처리
- Redis/Kafka 기반 메시징 확장 구조
- S3 연동과 LocalStack 기반 로컬 파일 저장 환경
- Flyway 기반 데이터베이스 마이그레이션 관리
- Swagger/OpenAPI 및 Markdown API 명세 정리

### 목차

- [기술 스택](#기술-스택)
- [핵심 기능](#핵심-기능)
- [아키텍처 및 데이터베이스 설계](#아키텍처-및-데이터베이스-설계)
- [핵심 트러블 슈팅 및 성과](#핵심-트러블-슈팅-및-성과)
- [빌드 방법](#빌드-방법)


---

## 기술 스택

| 구분 | 사용 기술 |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 3.5.7 |
| Build | Gradle 9, Multi Module |
| Persistence | Spring Data JPA, Querydsl |
| Database | PostgreSQL |
| Cache / Messaging | Redis, Apache Kafka |
| Auth | Spring Security, JWT |
| Storage | AWS S3 SDK, LocalStack |
| Migration | Flyway |
| API Docs | springdoc-openapi, Swagger UI, Markdown API Spec |
| Test | JUnit 5, AssertJ, Spring Boot Test, Testcontainers |
| Infra | Docker, Docker Compose |


---

## 핵심 기능

### 1. 인증과 사용자 관리

- 회원가입과 로그인 API 제공
- JWT 기반 인증 처리
- 내 프로필 상태 메시지 수정
- 프로필 이미지 업로드 및 삭제

### 2. 채팅방과 메시지 흐름

- 채팅방 생성, 목록 조회, 상세 조회, 수정, 삭제
- 메시지 전송, 수정, 삭제
- 채팅 history 조회
- 메시지 읽음 처리
- SSE 기반 채팅 stream subscribe 엔드포인트 제공

### 3. 파일 업로드

- 채팅 파일 업로드
- 파일 목록 조회
- 파일 삭제

### 4. 문서화와 검증

- Swagger UI 제공
- Markdown 기반 API 명세 문서 정리
- Flyway migration 관리
- Testcontainers 기반 통합 테스트 환경 구성

---


## 트러블 슈팅 및 성과

### 1. 유니크 키를 활용해 조회 성능과 데이터 정합성을 함께 개선

채팅 서비스는 사용자, 채팅방, 참여자처럼 같은 데이터를 반복적으로 조회하는 구간이 많고,  
중복 데이터가 한 번 들어가면 이후 조회 로직과 예외 처리까지 함께 복잡해지기 쉽습니다.

이 프로젝트에서는 자주 식별 조건으로 사용되는 컬럼 조합에 유니크 키를 두어,  
애플리케이션 코드에서 중복 여부를 매번 검사하기보다 데이터베이스가 먼저 정합성을 보장하도록 설계했습니다.

이 과정에서 얻은 점은 두 가지였습니다.

- 중복 데이터 삽입 가능성을 DB 레벨에서 차단해 서비스 로직을 단순하게 유지할 수 있었습니다.
- 조회 조건이 명확해지면서 인덱스 활용 가능성이 높아져, 반복 조회 구간의 성능을 더 안정적으로 가져갈 수 있었습니다.

즉, 유니크 키를 단순 제약 조건이 아니라 성능과 정합성을 함께 챙기는 설계 요소로 활용했습니다.

- 관련 Docs : [`qootalk-server/docs/unique-key-design.md`](./qootalk-server/docs/unique-key-design.md)


### 2. Kafka와 Redis를 함께 사용해 동시성 처리 구조를 강화

채팅 시스템은 여러 사용자가 동시에 같은 채팅방에서 메시지를 보내고 읽음 이벤트를 발생시키는 구조이기 때문에,  
모든 처리를 단일 요청-응답 흐름에만 묶어두면 부하가 집중되기 쉽고 실시간 처리 책임도 한곳에 몰리게 됩니다.

이 프로젝트에서는 Redis와 Kafka를 역할별로 분리해 사용했습니다.

- Redis는 사용자 presence 관리와 빠른 실시간 전달 보조 채널 역할을 담당합니다.
- Kafka는 채팅 메시지와 읽음 이벤트를 비동기적으로 흘려보내는 메시징 채널 역할을 담당합니다.

이렇게 분리하면서 요청 처리와 이벤트 전달 책임을 나눌 수 있었고,  
동시 요청이 몰리는 상황에서도 애플리케이션이 한 번에 모든 실시간 처리를 직접 떠안지 않도록 구조를 정리할 수 있었습니다.

결과적으로 실시간성은 유지하면서도, 이후 소비자 확장이나 이벤트 기반 기능 추가로 이어질 수 있는 기반을 만들었습니다.

- 관련 Docs : [`qootalk-server/docs/chat-redis-kafka-optimization.md`](./qootalk-server/docs/chat-redis-kafka-optimization.md)

### 3. Testcontainers를 활용해 실제 실행 환경에 가까운 테스트를 구성

이 프로젝트는 PostgreSQL, Redis, Kafka, LocalStack처럼 외부 인프라 의존성이 많기 때문에,  
로컬에 설치된 환경만 믿고 테스트하면 개발자마다 결과가 달라질 가능성이 컸습니다.

이를 줄이기 위해 Testcontainers를 도입해 테스트 시점에 필요한 인프라를 컨테이너로 직접 띄우고,  
애플리케이션이 실제와 유사한 조건에서 동작하는지 검증할 수 있도록 구성했습니다.

적용 범위

- PostgreSQL 기반 영속성 테스트
- Redis 연동 테스트
- Kafka 메시징 테스트
- LocalStack 기반 S3 테스트
- Flyway migration 검증

이 방식의 장점은 단순 단위 테스트로는 놓치기 쉬운 설정 오류, 연결 문제, 마이그레이션 누락을  
개발 단계에서 빠르게 확인할 수 있다는 점이었습니다.  
덕분에 테스트가 “코드만 검증하는 단계”를 넘어 “실행 환경을 검증하는 단계”까지 확장되었습니다.

### 4. 클린 아키텍처 기반 설계로 교체 비용을 낮추고 도메인 분리를 강화

채팅 서버는 인증, 파일 저장, 메시징, 영속성처럼 외부 기술 의존성이 많아서,  
구현을 빠르게 붙이다 보면 비즈니스 규칙과 기술 코드가 쉽게 뒤섞이게 됩니다.

이 프로젝트에서는 클린 아키텍처와 포트-어댑터 구조를 기준으로 모듈을 나누고,  
도메인 규칙은 `domain`, 유스케이스는 `application`, 기술 구현은 `infrastructure`, 진입 계층은 `presentation`에 배치했습니다.

이 설계를 통해 얻은 효과는 다음과 같습니다.

- 비즈니스 규칙이 특정 프레임워크나 저장소 구현에 직접 의존하지 않게 만들 수 있었습니다.
- Redis, Kafka, S3, JWT 같은 인프라 구현을 교체하더라도 수정 범위를 상대적으로 작게 유지할 수 있었습니다.
- 도메인 로직과 외부 연동 책임이 분리되어 테스트 작성과 유지보수가 더 쉬워졌습니다.

결국 이 구조는 단순히 “코드를 예쁘게 나누는 것”보다,  
기능이 늘어나도 변경 영향 범위를 통제하고 확장 가능한 채팅 백엔드를 만들기 위한 설계 선택이었습니다.



---


## 아키텍처 설계

### 멀티모듈 구조

이 프로젝트는 역할별 책임을 나누기 위해 멀티모듈 구조를 사용합니다.

| 모듈 | 역할 |
| --- | --- |
| `module-domain` | 엔티티, 도메인 규칙, Repository 인터페이스 |
| `module-application` | Usecase, Command/Result DTO, Port in/out, 서비스 조합 |
| `module-infrastructure` | JPA, Redis, Kafka, S3, JWT, Querydsl, Flyway 구현 |
| `module-presentation` | Spring Boot 진입점, Controller, Security, Swagger 설정 |
| `module-common` | 공통 응답 포맷, 공통 예외, 에러 코드 |

`domain -> application -> infrastructure/presentation` 방향의 의존성을 가지고 있습니다.

### 요청 처리 흐름

```text
Client
  -> Presentation (Controller, DTO, Security)
  -> Application (UseCase, Service, Port)
  -> Domain (Entity, Rule, Repository Interface)
  -> Infrastructure (JPA, Redis, Kafka, S3, JWT)
  -> PostgreSQL / Redis / Kafka / S3(LocalStack)
```

### 메시징 구조

채팅 기능은 단순히 DB에 저장하는 수준에서 끝나지 않도록, 다음 인프라 구성을 함께 사용합니다.

- Redis: 사용자 presence 및 pub/sub 기반 메시징 보조 역할
- Kafka: 채팅 메시지와 읽음 이벤트 전달을 위한 비동기 메시징 채널
- SSE: 클라이언트 스트림 구독 인터페이스 제공

---

## 빌드 방법

### 1. 사전 준비

- Docker / Docker Compose
- Java 21

Docker Compose만 사용할 경우 Java는 로컬에 없어도 되지만,  
Gradle로 직접 실행하거나 테스트를 돌릴 예정이라면 Java 21이 필요합니다.

### 2. Docker Compose로 실행

```bash
cd qootalk-server
docker compose up --build
```

백그라운드 실행

```bash
cd qootalk-server
docker compose up -d --build
```

종료

```bash
cd qootalk-server
docker compose down -v
```

### 3. Gradle로 직접 실행

```bash
cd qootalk-server
./gradlew :module-presentation:bootRun
```

테스트 실행

```bash
cd qootalk-server
./gradlew test
```

### 4. 참고 문서

- 통합 API 문서: [`qootalk-server/docs/API_SPEC_V1.md`](./qootalk-server/docs/API_SPEC_V1.md)
- 세부 API 명세: [`qootalk-server/docs/api-spec/README.md`](./qootalk-server/docs/api-spec/README.md)
