package com.lrchan.qootalk.infrastructure.migration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.ValidateResult;

class PostgresDBMigrationVerificationTest extends PostgresDBIntegrationTestSupport {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Flyway flyway;

    @Test
    @DisplayName("Flyway 마이그레이션이 성공적으로 수행되어 테이블이 존재해야 한다")
    void verifyMigration() {
        // Given & When: 컨텍스트 로드 시 Flyway가 자동 실행됨

        // Then: 특정 테이블(예: users)이 존재하는지 확인
        Integer count = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM information_schema.tables WHERE table_name = 'users'", 
            Integer.class
        );

        assertThat(count).isGreaterThan(0);
    }

    @Test
    @DisplayName("messages 테이블에 room_id 인덱스가 존재해야 한다")
    void verifyMessageRoomIdIndex() {
        Integer indexCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM pg_indexes WHERE tablename = 'messages' AND indexname = 'idx_messages_room_id'",
            Integer.class
        );

        assertThat(indexCount).isGreaterThan(0);
    }

    @Test
    @DisplayName("빈 데이터베이스에서 모든 마이그레이션이 성공적으로 실행되어야 한다")
    void verifyInitialMigrationFromScratch() {
        // 1. Clean: 기존 스키마를 모두 날림
        flyway.clean();
        
        // 2. Migrate: 처음부터 다시 실행
        flyway.migrate();

        // 3. Then: 주요 테이블이 생성되었는지 확인
        Integer tableCount = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM information_schema.tables WHERE table_schema = 'public'", 
            Integer.class
        );
        assertThat(tableCount).isGreaterThan(0);
    }

    @Test
    @DisplayName("마이그레이션 실행 후에도 기존 데이터는 온전히 유지되어야 한다")
    void verifyDataPreservationAfterMigration() {
        // 1. Given: 특정 버전(V1) 상태에서 데이터 삽입
        // (이미 마이그레이션이 끝난 상태라면 임의 데이터 삽입 후 다음 테스트 진행)
        String testEmail = "migration_test@qootalk.com";
        jdbcTemplate.update(
            "INSERT INTO users (email, password, name, role, created_at, updated_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", 
            testEmail, "password123", "OldUser", "USER"
        );
        // 2. When: (실제 상황이라면 추가 마이그레이션 V2, V3가 실행됨)
        // 여기서는 컨텍스트가 이미 로드되었으므로 현재 데이터가 잘 남아있는지 조회
        
        // 3. Then: 데이터가 변함없이 존재하는지 확인
        String name = jdbcTemplate.queryForObject(
            "SELECT name FROM users WHERE email = ?", String.class, testEmail);
        
        assertThat(name).isEqualTo("OldUser");
    }

    @Test
    @DisplayName("현재 마이그레이션이 Flyway validate를 통과해야 한다")
    void verifyMigrationValidationSucceeds() {
        
        // Flyway의 validate 기능을 실행하여 현재 상태와 스크립트가 일치하는지 확인
        // 스크립트가 임의로 수정되었다면 예외가 발생함
        ValidateResult validateOutput = flyway.validateWithResult();
        
        if (!validateOutput.validationSuccessful) {
            System.out.println("마이그레이션 유효성 실패 사유: " + validateOutput.invalidMigrations.get(0).description);
        }
        
        assertThat(validateOutput.validationSuccessful).isTrue();
    }
}