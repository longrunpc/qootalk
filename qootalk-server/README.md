# QooTalk

## 서비스 소개

쿠톡은 사내 커뮤니케이션을 더 빠르고 가볍게 만들어주는 실시간 메시징 플랫폼입니다.

QooTalk is a real-time messaging platform that makes internal communication faster, lighter, and more connected.

본 서비스는 텍스트 채팅, 그룹 채팅, 파일 공유, 알림 기능을 포함하여 업무 현장에서 필요한 소통 기능을 제공하며, **보안성**과 **확장성**을 최우선으로 고려하였습니다.

### 주요 특징

1. **실시간 메시징**
   - WebSocket 기반 양방향 통신을 통한 지연 없는 대화
   - 개인/그룹 채팅 지원  

2. **협업 기능 강화**
   - 파일 및 이미지 공유
   - 메시지 검색 및 핀 고정 기능
   - 프로젝트/팀 단위 채팅방 운영  

3. **보안성 확보**
   - 사용자 인증 및 권한 관리
   - 메시지 암호화 및 로그 관리
   - 외부 유출 방지 기능  

4. **운영 효율성**
   - Redis를 통한 세션/캐시 관리로 성능 최적화
   - Kafka 기반 메시지 큐로 안정적인 이벤트 처리
   - Swagger(OpenAPI)로 API 문서화 및 개발자 친화적 환경 제공  

---

## 아키텍처

QooTalk은 **멀티모듈 기반 Clean Architecture**를 적용하여 모듈 간 역할을 명확히 분리하고, 유지보수성과 확장성을 강화했습니다.


### 📦 모듈 구성

- **module-domain**  
  비즈니스 핵심 규칙과 모델 정의

- **module-application**  
  유즈케이스(Use Case) 단위의 비즈니스 흐름 제어 계층  

- **module-infrastructure**  
  외부 시스템 연동 및 기술 구현 계층  

- **module-presentation**  
  REST API 제공 및 사용자 진입 계층
  
- **module-common**
   전 계층에서 공통으로 사용하는 유틸리티 및 기본 구성 계층
---

## 기술 스택

- **Backend Framework**: Spring Boot
- **Database**: PostgreSQL  
- **Caching & Session**: Redis  
- **Messaging**: Apache Kafka  
- **API 문서화**: Swagger
- **Containerization**: Docker & Docker Compose  

---
