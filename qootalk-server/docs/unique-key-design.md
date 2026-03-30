# Unique Key 선정 이유

이 문서는 QooTalk Server에서 유니크 키를 어떤 기준으로 선정했는지, 그리고 각 유니크 키가 도메인 규칙과 성능 측면에서 어떤 의미를 가지는지 정리한 문서입니다.

## 선정 기준

이 프로젝트에서 유니크 키는 단순히 중복을 막기 위한 제약이 아니라 아래 조건을 만족하는 경우에만 두도록 판단했습니다.

- 도메인상 절대로 중복되면 안 되는 식별 조건인가
- 애플리케이션이 같은 조건으로 반복 조회하거나 존재 여부를 검사하는가
- 중복 방지 로직을 서비스 코드보다 DB가 맡는 편이 더 단순하고 안전한가
- 인덱스 유지 비용보다 조회 안정성과 정합성 이점이 더 큰가

즉, `정합성 보장`이 1차 목적이고, `조회 최적화`와 `쓰기 로직 단순화`는 그 다음으로 얻는 이점으로 판단했습니다.

## 1. `uk_users_email`

대상 컬럼: `users(email)`

관련 스키마:
- [`V1_1__users.sql`](/Users/longrunpc/projects/qootalk/qootalk-server/module-infrastructure/src/main/resources/db/migration/V1_1__users.sql)
- [`UserEntity.java`](/Users/longrunpc/projects/qootalk/qootalk-server/module-infrastructure/src/main/java/com/lrchan/qootalk/infrastructure/persistence/user/UserEntity.java)

선정 이유:

- 이메일은 회원을 식별하는 대표 로그인 키라서 중복 허용이 비즈니스적으로 맞지 않습니다.
- 회원가입 시 같은 이메일이 두 번 들어가면 계정 식별, 인증, 비밀번호 검증 흐름이 모두 불안정해집니다.
- 애플리케이션에서 `findByEmail`, `existsByEmail`처럼 단건 조회가 자주 발생하므로 인덱스 기반 탐색 이점이 큽니다.

기대한 효과:

- 회원가입 중복 검사를 DB가 최종 보장합니다.
- 로그인/사용자 조회 시 단건 검색 비용을 낮출 수 있습니다.
- 이메일 중복으로 인한 예외 분기와 데이터 정리 비용을 줄일 수 있습니다.

정리하면, `users.email`은 “중복되면 안 되는 자연키”이면서 동시에 “자주 조회되는 식별 조건”이라 유니크 키로 두는 타당성이 가장 높은 케이스입니다.

## 2. `uk_room_participants_user_room`

대상 컬럼: `room_participants(user_id, room_id)`

관련 스키마:
- [`V1_3__room_participants.sql`](/Users/longrunpc/projects/qootalk/qootalk-server/module-infrastructure/src/main/resources/db/migration/V1_3__room_participants.sql)
- [`RoomParticipantEntity.java`](/Users/longrunpc/projects/qootalk/qootalk-server/module-infrastructure/src/main/java/com/lrchan/qootalk/infrastructure/persistence/chat/participant/RoomParticipantEntity.java)

선정 이유:

- 한 사용자는 같은 채팅방에 참여자로 한 번만 존재해야 합니다.
- 같은 `(user_id, room_id)` 조합이 중복되면 읽음 처리, 권한(role), 알림 설정(notification) 값이 어느 행을 기준으로 해야 하는지 모호해집니다.
- `user_id` 컬럼은 unique 하기 때문에, 앞 순서로 배치하였습니다.
- 애플리케이션에서 `findByUserIdAndRoomId`, `existsByUserIdAndRoomId` 패턴이 자연스럽게 나오기 때문에 복합 조건 조회의 인덱스 효율이 좋습니다.

기대한 효과:

- 채팅방 참여 관계를 DB 레벨에서 유일하게 유지할 수 있습니다.
- 참여 여부 확인, 초대 중복 방지, 읽음 상태 갱신 로직이 단순해집니다.
- 복합 조건 단건 조회에서 전체 스캔을 피할 가능성이 커집니다.

이 유니크 키는 단순 중복 방지 이상의 의미를 갖습니다. 채팅 참여라는 관계 자체를 하나의 유일한 사실로 보장하기 때문에, 도메인 정합성과 조회 성능을 함께 잡는 설계 포인트입니다.

## 3. `uk_message_mentions_message_user`

대상 컬럼: `message_mentions(message_id, user_id)`

관련 스키마:
- [`V1_5__message_mentions.sql`](/Users/longrunpc/projects/qootalk/qootalk-server/module-infrastructure/src/main/resources/db/migration/V1_5__message_mentions.sql)
- [`MessageEntity.java`](/Users/longrunpc/projects/qootalk/qootalk-server/module-infrastructure/src/main/java/com/lrchan/qootalk/infrastructure/persistence/chat/message/MessageEntity.java)

선정 이유:

- 하나의 메시지에서 같은 사용자를 여러 번 멘션하는 것은 도메인상 의미가 없습니다.
- 중복 멘션이 허용되면 알림 발송, 멘션 표시, 멘션 수 계산에서 불필요한 중복 처리가 생깁니다.
- `(message_id, user_id)`가 유일하면 메시지별 멘션 목록을 깔끔하게 관리할 수 있습니다.

기대한 효과:

- 동일 메시지 내 중복 멘션 저장을 원천 차단합니다.
- 멘션 알림 발송 시 중복 전송 위험을 줄입니다.
- 애플리케이션에서 멘션 deduplication 로직을 매번 두지 않아도 됩니다.

이 유니크 키는 조회 성능 이득보다도 “한 메시지에서 같은 사람을 중복 멘션하지 않는다”는 규칙을 데이터 구조 자체에 녹였다는 점이 더 중요합니다.

## 한 줄 정리

이 프로젝트의 유니크 키는 “자주 조회되기 때문에”가 아니라, 먼저 “도메인상 유일해야 하는 사실”에만 두고, 그 결과로 조회 성능과 로직 단순화까지 함께 얻도록 선정했습니다.
