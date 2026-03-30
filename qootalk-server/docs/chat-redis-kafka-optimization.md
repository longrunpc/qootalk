# Redis와 Kafka를 활용한 채팅 서비스 최적화

이 문서는 QooTalk Server의 현재 구현을 기준으로, Redis와 Kafka를 활용해 채팅 관련 서비스를 어떻게 최적화했는지 정리한 문서입니다.

## 왜 Redis와 Kafka를 함께 사용했는가

채팅 서비스는 단순 CRUD보다 다음 요구사항이 더 중요합니다.

- 메시지를 저장한 뒤 빠르게 사용자에게 전달해야 한다
- 같은 방의 여러 사용자에게 동시에 이벤트를 전파해야 한다
- 읽음 처리 같은 이벤트도 실시간으로 흘려야 한다
- 접속 중인 사용자와 비접속 사용자를 구분해 불필요한 전달 비용을 줄여야 한다

이 프로젝트에서는 이 문제를 `DB 저장`, `이벤트 발행`, `실시간 전달`로 나눠서 처리했습니다.

- PostgreSQL: 영속 데이터 저장과 정합성 보장
- Kafka: 채팅/읽음 이벤트의 비동기 전달
- Redis Pub/Sub: 현재 연결된 서버 인스턴스까지의 빠른 실시간 fan-out
- Redis Presence: 어떤 사용자가 현재 연결 중인지 판단
- SSE: 최종적으로 클라이언트에 이벤트 전달

즉, 요청 처리와 실시간 전파 책임을 분리해서 애플리케이션이 한 번에 모든 부담을 떠안지 않도록 구성했습니다.

## 전체 흐름

### 1. 메시지 전송 흐름

관련 코드:
- [`SendMessageService.java`](/Users/longrunpc/projects/qootalk/qootalk-server/module-application/src/main/java/com/lrchan/qootalk/application/chat/service/SendMessageService.java)
- [`KafkaChatMessagePublisher.java`](/Users/longrunpc/projects/qootalk/qootalk-server/module-infrastructure/src/main/java/com/lrchan/qootalk/infrastructure/messaging/kafka/KafkaChatMessagePublisher.java)
- [`ChatMessageKafkaListener.java`](/Users/longrunpc/projects/qootalk/qootalk-server/module-infrastructure/src/main/java/com/lrchan/qootalk/infrastructure/messaging/kafka/ChatMessageKafkaListener.java)
- [`RedisChatMessageSubscriber.java`](/Users/longrunpc/projects/qootalk/qootalk-server/module-infrastructure/src/main/java/com/lrchan/qootalk/infrastructure/messaging/redis/RedisChatMessageSubscriber.java)
- [`ChatStreamController.java`](/Users/longrunpc/projects/qootalk/qootalk-server/module-presentation/src/main/java/com/lrchan/qootalk/presentation/api/chat/controller/ChatStreamController.java)

흐름:

1. `SendMessageService`가 사용자, 채팅방, 참여자, 부모 메시지, 멘션, 첨부파일을 검증합니다.
2. 메시지를 DB에 저장하고 첨부파일과 읽음 상태를 갱신합니다.
3. 저장 직후 `PublishChatMessagePort`를 통해 `ChatMessageEvent`를 Kafka로 발행합니다.
4. `ChatMessageKafkaListener`가 Kafka 이벤트를 받습니다.
5. 해당 방 참여자 중 현재 Redis presence에 연결되어 있는 사용자만 추려냅니다.
6. 각 사용자별 `UserChatMessageEvent`로 변환해 Redis Pub/Sub 채널로 발행합니다.
7. 각 애플리케이션 인스턴스의 `RedisChatMessageSubscriber`가 자기 프로세스의 로컬 SSE 연결로 전달합니다.

핵심 최적화 포인트:

- HTTP 요청 안에서 모든 실시간 전달을 직접 처리하지 않습니다.
- 메시지 저장 성공 후 브로드캐스팅을 Kafka로 넘겨 요청 경로를 짧게 유지합니다.
- Kafka 단계에서 방 참여자 전체를 기준으로 fan-out 대상을 정하되, Redis presence를 통해 실제 연결 중인 사용자만 남깁니다.
- Redis Pub/Sub는 최종 실시간 전달 계층으로 사용해, 현재 살아 있는 서버 인스턴스들에 빠르게 이벤트를 흘립니다.

정리하면, `저장`과 `전달`을 분리해 사용자 요청 latency를 낮추고, 온라인 사용자에게만 실시간 전송하는 구조로 최적화했습니다.

## 읽음 처리 최적화

관련 코드:
- [`MarkMessageReadService.java`](/Users/longrunpc/projects/qootalk/qootalk-server/module-application/src/main/java/com/lrchan/qootalk/application/chat/service/MarkMessageReadService.java)
- [`KafkaReadReceiptPublisher.java`](/Users/longrunpc/projects/qootalk/qootalk-server/module-infrastructure/src/main/java/com/lrchan/qootalk/infrastructure/messaging/kafka/KafkaReadReceiptPublisher.java)
- [`ReadReceiptKafkaListener.java`](/Users/longrunpc/projects/qootalk/qootalk-server/module-infrastructure/src/main/java/com/lrchan/qootalk/infrastructure/messaging/kafka/ReadReceiptKafkaListener.java)
- [`RedisReadReceiptSubscriber.java`](/Users/longrunpc/projects/qootalk/qootalk-server/module-infrastructure/src/main/java/com/lrchan/qootalk/infrastructure/messaging/redis/RedisReadReceiptSubscriber.java)

흐름:

1. `MarkMessageReadService`가 사용자, 채팅방, 참여자, 메시지를 검증합니다.
2. 이미 읽은 위치보다 뒤로 가지 않는 경우에는 즉시 종료합니다.
3. 실제 갱신이 필요한 경우에만 참여자의 `lastReadMessageId`를 저장합니다.
4. 그 다음 `ReadReceiptEvent`를 Kafka로 발행합니다.
5. `ReadReceiptKafkaListener`가 방 참여자 중 읽은 본인 제외, 현재 온라인인 사용자만 골라 Redis로 전달합니다.
6. `RedisReadReceiptSubscriber`가 로컬 SSE 연결에 `read-receipt` 이벤트를 보냅니다.

핵심 최적화 포인트:

- 읽음 위치가 이전 값보다 작거나 같으면 바로 반환해 불필요한 DB 쓰기와 이벤트 발행을 막습니다.
- 읽음 이벤트도 메시지 전송과 같은 비동기 구조를 따라가므로, 읽음 처리 API가 브로드캐스트 비용까지 직접 부담하지 않습니다.
- 읽은 사람 본인에게는 다시 보내지 않고, 실제 온라인 사용자에게만 전달합니다.

즉, 읽음 처리에서는 `불필요한 갱신 제거`와 `비동기 fan-out` 두 가지를 함께 적용했습니다.

## Redis Presence로 온라인 사용자만 선별

관련 코드:
- [`RedisUserPresenceTracker.java`](/Users/longrunpc/projects/qootalk/qootalk-server/module-infrastructure/src/main/java/com/lrchan/qootalk/infrastructure/messaging/presence/RedisUserPresenceTracker.java)
- [`LocalUserConnectionRegistry.java`](/Users/longrunpc/projects/qootalk/qootalk-server/module-infrastructure/src/main/java/com/lrchan/qootalk/infrastructure/messaging/presence/LocalUserConnectionRegistry.java)
- [`UserPresenceHeartbeatScheduler.java`](/Users/longrunpc/projects/qootalk/qootalk-server/module-infrastructure/src/main/java/com/lrchan/qootalk/infrastructure/messaging/presence/UserPresenceHeartbeatScheduler.java)
- [`RedisPubSubConfig.java`](/Users/longrunpc/projects/qootalk/qootalk-server/module-infrastructure/src/main/java/com/lrchan/qootalk/infrastructure/config/RedisPubSubConfig.java)

구성 방식:

- SSE 연결이 생성되면 `LocalUserConnectionRegistry`에 연결을 등록합니다.
- 동시에 `RedisUserPresenceTracker`가 `qootalk:user:presence:{userId}:{connectionId}` 형태의 키를 TTL과 함께 저장합니다.
- 스케줄러가 주기적으로 heartbeat를 보내면서 TTL을 갱신합니다.
- 연결 종료, timeout, error가 발생하면 로컬 레지스트리와 Redis 키를 함께 정리합니다.

최적화 의미:

- 어떤 인스턴스에 연결되어 있는지와 상관없이 Redis를 기준으로 온라인 여부를 공유할 수 있습니다.
- Kafka 리스너가 방 참여자 전체에게 무조건 fan-out하지 않고, 실제 접속 중인 사용자만 필터링할 수 있습니다.
- 여러 서버 인스턴스로 확장되더라도 presence 기준을 중앙화할 수 있습니다.

즉, Redis는 단순 캐시가 아니라 “실시간 전달 대상 선별기” 역할을 하도록 사용했습니다.

## Redis Pub/Sub를 최종 fan-out 계층으로 둔 이유

관련 코드:
- [`RedisMessagePublisher.java`](/Users/longrunpc/projects/qootalk/qootalk-server/module-infrastructure/src/main/java/com/lrchan/qootalk/infrastructure/messaging/redis/RedisMessagePublisher.java)
- [`RedisChatMessageSubscriber.java`](/Users/longrunpc/projects/qootalk/qootalk-server/module-infrastructure/src/main/java/com/lrchan/qootalk/infrastructure/messaging/redis/RedisChatMessageSubscriber.java)
- [`RedisReadReceiptSubscriber.java`](/Users/longrunpc/projects/qootalk/qootalk-server/module-infrastructure/src/main/java/com/lrchan/qootalk/infrastructure/messaging/redis/RedisReadReceiptSubscriber.java)

Kafka만으로도 이벤트를 퍼뜨릴 수 있지만, 이 프로젝트에서는 Redis Pub/Sub를 한 단계 더 둬서 “현재 살아 있는 SSE 연결”에 가깝게 이벤트를 전달합니다.

그 이유는 다음과 같습니다.

- Kafka는 이벤트 스트림과 소비 확장에 유리합니다.
- Redis Pub/Sub는 지금 연결된 프로세스에 즉시 전달하는 데 가볍습니다.
- 로컬 SSE 연결은 결국 각 애플리케이션 인스턴스 메모리에 있으므로, 마지막 전달은 해당 인스턴스가 직접 처리해야 합니다.

결과적으로 구조는 아래처럼 역할이 나뉩니다.

- Kafka: 방 단위 이벤트를 시스템 전체로 비동기 전파
- Redis Pub/Sub: 사용자별 이벤트를 현재 실행 중인 애플리케이션 인스턴스로 빠르게 중계
- Local registry + SSE: 실제 HTTP 스트림으로 최종 전달

이렇게 분리하면 이벤트 백본과 실시간 세션 전달 계층을 섞지 않고 운영할 수 있습니다.

## SSE 연결 유지와 로컬 디스패치 최적화

관련 코드:
- [`ChatStreamController.java`](/Users/longrunpc/projects/qootalk/qootalk-server/module-presentation/src/main/java/com/lrchan/qootalk/presentation/api/chat/controller/ChatStreamController.java)
- [`LocalUserConnectionRegistry.java`](/Users/longrunpc/projects/qootalk/qootalk-server/module-infrastructure/src/main/java/com/lrchan/qootalk/infrastructure/messaging/presence/LocalUserConnectionRegistry.java)

적용 내용:

- 사용자별로 여러 SSE 연결을 `ConcurrentHashMap` 기반 레지스트리에 보관합니다.
- 이벤트가 오면 해당 사용자에게 연결된 emitter들만 순회합니다.
- heartbeat 이벤트를 보내 연결 생존 여부를 유지합니다.
- `IOException`, timeout, completion 시 연결을 즉시 정리합니다.

최적화 의미:

- 전체 연결을 매번 탐색하지 않고 사용자 단위로 바로 접근할 수 있습니다.
- 죽은 연결을 빨리 정리해 메모리 누수와 불필요한 전송 시도를 줄입니다.
- 한 사용자의 복수 클라이언트 연결도 같은 모델로 처리할 수 있습니다.


## 한 줄 정리

이 프로젝트에서 Redis와 Kafka는 “채팅 기능에 인프라를 억지로 붙인 것”이 아니라, 메시지 저장과 실시간 전파를 분리하고, 온라인 사용자에게만 이벤트를 fan-out하며, 멀티 인스턴스 환경에서도 SSE 전달을 안정적으로 유지하기 위한 역할 분담 구조로 사용되고 있습니다.
