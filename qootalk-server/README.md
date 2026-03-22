# QooTalk Server

## 프로젝트 설명 및 목차

QooTalk Server는 사내 커뮤니케이션을 위한 채팅 백엔드 서버입니다.  
현재 프로젝트는 인증, 사용자 프로필, 채팅방, 파일 첨부 기능을 중심으로 구현되어 있으며, 멀티모듈 기반의 계층 분리를 통해 유지보수성과 확장성을 높이는 데 초점을 맞췄습니다.

- JWT 기반 로그인 및 인증 처리
- 사용자 회원가입, 상태 메시지 수정, 프로필 이미지 업로드/삭제
- 채팅방 생성, 목록 조회, 상세 조회, 수정, 삭제
- 채팅 파일 업로드, 목록 조회, 삭제
- Swagger/OpenAPI 및 마크다운 API 명세 관리
- Flyway 기반 DB 마이그레이션 관리
- Redis refresh token 저장소 연동
- S3 연동 및 LocalStack 기반 로컬/테스트 환경 구성

### 목차

- [기술 스택](#기술-스택)
- [아키텍처 및 설계](#아키텍처-및-설계)
- [핵심 경험 및 트러블 슈팅](#핵심-경험-및-트러블-슈팅)
- [시작 가이드](#시작-가이드)

## 기술 스택

| 구분 | 사용 기술 |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 3.5.7 |
| Persistence | Spring Data JPA, Querydsl |
| Database | PostgreSQL |
| Cache | Redis |
| Auth | Spring Security, JWT |
| Storage | AWS S3, LocalStack |
| Migration | Flyway |
| API Docs | springdoc-openapi, Swagger UI, Markdown API Spec |
| Test | JUnit 5, AssertJ, Spring Boot Test, Testcontainers |

## 아키텍처 및 설계

이 프로젝트는 `domain -> application -> infrastructure/presentation` 방향의 의존성을 유지하는 멀티모듈 구조를 사용합니다.  
핵심 비즈니스 규칙은 도메인에 두고, 유스케이스 조합은 애플리케이션 계층에서 처리하며, 외부 기술 의존성은 인프라 계층으로 분리했습니다.

### 모듈 구조

| 모듈 | 역할 |
| --- | --- |
| `module-domain` | 엔티티, VO, 도메인 규칙, Repository 인터페이스 |
| `module-application` | Usecase, Command/Result DTO, Port in/out, 서비스 로직 |
| `module-infrastructure` | JPA/Redis/S3/JWT/Querydsl/Flyway 등 기술 구현체 |
| `module-presentation` | Spring Boot 진입점, REST Controller, Swagger, Security 설정 |
| `module-common` | 공통 응답 포맷, 공통 예외, 에러 코드, 공용 Port |

### 설계 포인트

1. **포트-어댑터 기반 유스케이스 분리**
   `module-application`에서 `port.in`, `port.out`으로 인터페이스를 정의하고, 구현은 `module-infrastructure`가 담당합니다.  
   덕분에 비즈니스 흐름이 외부 기술 세부사항에 직접 묶이지 않도록 구성했습니다.

2. **공통 응답/예외 규격 통일**
   [`docs/api-spec/README.md`](./docs/api-spec/README.md)에 정리된 형식처럼 `ApiResponse`, `PagedResponse`, `GlobalExceptionHandler`를 통해 성공/실패 응답 형식을 통일했습니다.

3. **보안 책임 분리**
   JWT 생성과 검증은 인프라 계층에서 처리하고, 프레젠테이션 계층은 인증 사용자 식별과 보안 필터 체인 구성에 집중하도록 나눴습니다.

4. **운영 변경 추적**
   DB 스키마는 Flyway 마이그레이션으로 버전 관리하고, 주요 도메인 변경 이력을 점진적으로 누적할 수 있도록 감사 로그 도메인도 분리했습니다.

### 요청 흐름

```text
Client
  -> Presentation(Controller, Security, DTO)
  -> Application(Usecase, Service, Port)
  -> Domain(Entity, VO, Rule, Repository Interface)
  -> Infrastructure(JPA, Redis, S3, JWT, Querydsl)
  -> PostgreSQL / Redis / S3
```

## 핵심 경험 및 트러블 슈팅

### 1. 멀티모듈 구조에서 역할이 섞이지 않도록 설계

기능이 늘어날수록 Controller, Service, Repository가 한 모듈에 몰리면 변경 영향 범위가 커지기 쉽습니다.  
이를 줄이기 위해 도메인 모델과 유스케이스, 외부 기술 구현을 모듈 단위로 분리했고, `port.out` 인터페이스를 기준으로 의존 방향을 고정했습니다.

**얻은 점**

- 비즈니스 규칙과 기술 구현을 분리해 테스트 대상을 더 명확하게 나눌 수 있었음
- Redis/S3/JPA 같은 구현을 바꿔도 유스케이스 코드 수정 범위를 줄일 수 있었음
- API 계층이 도메인 내부 구조를 직접 침범하지 않도록 방지할 수 있었음

### 2. 인증 상태를 JWT만으로 끝내지 않고 Redis와 함께 관리

Access Token과 Refresh Token을 함께 사용하면서, Refresh Token 저장소를 Redis로 분리했습니다.  
이 방식은 단순 JWT 발급보다 로그아웃, 재발급, 토큰 무효화 시나리오를 더 유연하게 처리할 수 있게 해줍니다.

**적용 내용**

- JWT 생성/검증 Provider 분리
- 인증 필터와 예외 필터 분리
- Refresh Token Redis Adapter 구성
- 로그인 시 쿠키 기반 토큰 전달 처리

### 3. S3 의존 기능을 로컬과 테스트에서 재현 가능하게 구성

파일 업로드 기능은 외부 스토리지 의존성이 강해서 개발 환경마다 재현 차이가 생기기 쉽습니다.  
이를 줄이기 위해 로컬에서는 LocalStack, 테스트에서는 Testcontainers LocalStack을 사용하도록 구성했습니다.

**적용 내용**

- `docker-compose.yml`로 LocalStack S3 실행
- S3 전용 설정 클래스 및 저장소 어댑터 분리
- 통합 테스트에서 S3 버킷 정리 루틴 포함

### 4. 스키마 변경 누락을 막기 위한 Flyway 검증

채팅방, 메시지, 첨부파일처럼 연관 테이블이 많은 구조에서는 DB 변경 누락이 쉽게 발생할 수 있습니다.  
이를 대비해 Flyway migration 스크립트를 버전별로 관리하고, 검증 테스트로 현재 마이그레이션 상태를 확인할 수 있게 했습니다.

**적용 내용**

- `V1_1__users.sql`부터 시작하는 버전 관리형 스키마 운영
- Flyway validate 기반 검증 테스트 작성
- 로컬/테스트 환경 모두 동일한 migration 흐름 유지

### 5. 조회성 API는 Querydsl 기반으로 확장 가능하게 설계

채팅방 목록, 파일 목록처럼 조건 조합과 페이징이 필요한 API는 단순 JPA 메서드만으로 유지하기 어려워집니다.  
이를 위해 Querydsl 설정과 Query Repository를 분리해 향후 필터 조건이 늘어나도 대응할 수 있게 구성했습니다.

## 시작 가이드

### 1. 사전 준비

- Java 21
- Docker / Docker Compose
- PostgreSQL
- Redis

현재 [`docker-compose.yml`](./docker-compose.yml)은 LocalStack(S3 에뮬레이터) 중심으로 구성되어 있습니다.  
따라서 로컬 실행 시 PostgreSQL과 Redis는 별도로 실행해야 합니다.

### 2. 환경 변수 설정

루트의 `.env` 파일을 사용하며, 로컬 실행 시 아래 항목이 필요합니다.

- `LOCAL_DB_URL`
- `LOCAL_DB_USERNAME`
- `LOCAL_DB_PASSWORD`
- `LOCAL_JPA_DDL_AUTO`
- `LOCAL_JPA_SHOW_SQL`
- `LOCAL_REDIS_HOST`
- `LOCAL_REDIS_PORT`
- `LOCAL_S3_ENDPOINT`
- `LOCAL_S3_REGION`
- `LOCAL_S3_ACCESS_KEY`
- `LOCAL_S3_SECRET_KEY`
- `LOCAL_S3_BUCKET_NAME`
- `JWT_SECRET`
- `JWT_ACCESS_EXPIRATION`
- `JWT_REFRESH_EXPIRATION`

운영 프로필까지 함께 관리하려면 `PROD_DB_*`, `PROD_S3_*` 값도 추가로 맞춰주면 됩니다.

### 3. LocalStack 실행

```bash
docker compose up -d
```

S3 초기화 스크립트는 [`scripts/localstack/init-s3.sh`](./scripts/localstack/init-s3.sh)를 사용합니다.

### 4. 애플리케이션 실행

```bash
./gradlew :module-presentation:bootRun
```

기본 실행 정보

- Server Port: `8080`
- Context Path: `/api/v1`
- Active Profile: `local`

### 5. API 문서 확인

- Swagger UI: `http://localhost:8080/api/v1/swagger-ui.html`
- OpenAPI Docs: `http://localhost:8080/api/v1/v3/api-docs`
- Markdown API Spec: [`docs/api-spec/README.md`](./docs/api-spec/README.md)

### 6. 참고 문서

- API 통합 명세: [`docs/API_SPEC_V1.md`](./docs/API_SPEC_V1.md)
- 세부 API 목록: [`docs/api-spec/README.md`](./docs/api-spec/README.md)
