package com.lrchan.qootalk.domain.chat.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Constructor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("FileSecurity VO 테스트")
class FileSecurityTest {

    private FileSecurity createFileSecurity(
            Visibility visibility,
            DownloadPolicy downloadPolicy,
            SharePolicy sharePolicy,
            ScanStatus scanStatus,
            Encryption encryption) {
        try {
            Constructor<FileSecurity> constructor = FileSecurity.class.getDeclaredConstructor(
                Visibility.class,
                DownloadPolicy.class,
                SharePolicy.class,
                ScanStatus.class,
                Encryption.class
            );
            constructor.setAccessible(true);
            return constructor.newInstance(visibility, downloadPolicy, sharePolicy, scanStatus, encryption);
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException("Failed to create FileSecurity", cause);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create FileSecurity", e);
        }
    }

    @Nested
    @DisplayName("생성")
    class CreateTest {

        @Test
        @DisplayName("유효한 보안 설정으로 생성할 수 있다")
        void should_CreateFileSecurity_When_ValidData() {
            // given
            Visibility visibility = Visibility.PRIVATE;
            DownloadPolicy downloadPolicy = DownloadPolicy.ALLOWED;
            SharePolicy sharePolicy = SharePolicy.DISABLED;
            ScanStatus scanStatus = ScanStatus.CLEAN;
            Encryption encryption = Encryption.NONE;

            // when
            FileSecurity fileSecurity = createFileSecurity(
                visibility,
                downloadPolicy,
                sharePolicy,
                scanStatus,
                encryption
            );

            // then
            assertThat(fileSecurity.visibility()).isEqualTo(visibility);
            assertThat(fileSecurity.downloadPolicy()).isEqualTo(downloadPolicy);
            assertThat(fileSecurity.sharePolicy()).isEqualTo(sharePolicy);
            assertThat(fileSecurity.scanStatus()).isEqualTo(scanStatus);
            assertThat(fileSecurity.encryption()).isEqualTo(encryption);
        }

        @Test
        @DisplayName("PUBLIC 파일로 생성할 수 있다")
        void should_CreateFileSecurity_When_PublicFile() {
            // given
            Visibility visibility = Visibility.PUBLIC;
            DownloadPolicy downloadPolicy = DownloadPolicy.ALLOWED;
            SharePolicy sharePolicy = SharePolicy.READ_ONLY;
            ScanStatus scanStatus = ScanStatus.CLEAN;
            Encryption encryption = Encryption.NONE;

            // when
            FileSecurity fileSecurity = createFileSecurity(
                visibility,
                downloadPolicy,
                sharePolicy,
                scanStatus,
                encryption
            );

            // then
            assertThat(fileSecurity.visibility()).isEqualTo(Visibility.PUBLIC);
            assertThat(fileSecurity.downloadPolicy()).isEqualTo(DownloadPolicy.ALLOWED);
        }

        @Test
        @DisplayName("암호화된 파일로 생성할 수 있다")
        void should_CreateFileSecurity_When_Encrypted() {
            // given
            Visibility visibility = Visibility.PRIVATE;
            DownloadPolicy downloadPolicy = DownloadPolicy.ALLOWED;
            SharePolicy sharePolicy = SharePolicy.DISABLED;
            ScanStatus scanStatus = ScanStatus.CLEAN;
            Encryption encryption = Encryption.AT_REST;

            // when
            FileSecurity fileSecurity = createFileSecurity(
                visibility,
                downloadPolicy,
                sharePolicy,
                scanStatus,
                encryption
            );

            // then
            assertThat(fileSecurity.encryption()).isEqualTo(Encryption.AT_REST);
        }
    }

    @Nested
    @DisplayName("팩토리 메서드")
    class FactoryMethodTest {

        @Test
        @DisplayName("defaultPrivate로 기본 비공개 보안 설정을 생성할 수 있다")
        void should_CreateDefaultPrivate_When_CallFactoryMethod() {
            // when
            FileSecurity fileSecurity = FileSecurity.defaultPrivate();

            // then
            assertThat(fileSecurity.visibility()).isEqualTo(Visibility.PRIVATE);
            assertThat(fileSecurity.downloadPolicy()).isEqualTo(DownloadPolicy.ALLOWED);
            assertThat(fileSecurity.sharePolicy()).isEqualTo(SharePolicy.DISABLED);
            assertThat(fileSecurity.scanStatus()).isEqualTo(ScanStatus.PENDING);
            assertThat(fileSecurity.encryption()).isEqualTo(Encryption.NONE);
        }

        @Test
        @DisplayName("publicReadable로 공개 읽기 가능 보안 설정을 생성할 수 있다")
        void should_CreatePublicReadable_When_CallFactoryMethod() {
            // when
            FileSecurity fileSecurity = FileSecurity.publicReadable();

            // then
            assertThat(fileSecurity.visibility()).isEqualTo(Visibility.PUBLIC);
            assertThat(fileSecurity.downloadPolicy()).isEqualTo(DownloadPolicy.ALLOWED);
            assertThat(fileSecurity.sharePolicy()).isEqualTo(SharePolicy.READ_ONLY);
            assertThat(fileSecurity.scanStatus()).isEqualTo(ScanStatus.CLEAN);
            assertThat(fileSecurity.encryption()).isEqualTo(Encryption.NONE);
        }
    }

    @Nested
    @DisplayName("검증 실패")
    class ValidationFailureTest {

        @Test
        @DisplayName("null 값이 포함되면 예외가 발생한다")
        void should_ThrowException_When_ContainsNull() {
            // given
            Visibility visibility = Visibility.PRIVATE;
            DownloadPolicy downloadPolicy = DownloadPolicy.ALLOWED;
            SharePolicy sharePolicy = SharePolicy.DISABLED;
            ScanStatus scanStatus = ScanStatus.CLEAN;
            Encryption encryption = Encryption.NONE;

            // when & then
            assertThatThrownBy(() -> createFileSecurity(null, downloadPolicy, sharePolicy, scanStatus, encryption))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("visibility");

            assertThatThrownBy(() -> createFileSecurity(visibility, null, sharePolicy, scanStatus, encryption))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("downloadPolicy");

            assertThatThrownBy(() -> createFileSecurity(visibility, downloadPolicy, null, scanStatus, encryption))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("sharePolicy");

            assertThatThrownBy(() -> createFileSecurity(visibility, downloadPolicy, sharePolicy, null, encryption))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("scanStatus");

            assertThatThrownBy(() -> createFileSecurity(visibility, downloadPolicy, sharePolicy, scanStatus, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("encryption");
        }

        @Test
        @DisplayName("악성 파일이 다운로드 가능하면 예외가 발생한다")
        void should_ThrowException_When_MaliciousFileWithDownloadAllowed() {
            // given
            Visibility visibility = Visibility.PRIVATE;
            DownloadPolicy downloadPolicy = DownloadPolicy.ALLOWED;
            SharePolicy sharePolicy = SharePolicy.DISABLED;
            ScanStatus scanStatus = ScanStatus.MALICIOUS;
            Encryption encryption = Encryption.NONE;

            // when & then
            assertThatThrownBy(() -> createFileSecurity(
                visibility,
                downloadPolicy,
                sharePolicy,
                scanStatus,
                encryption
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Malicious file cannot be downloadable or shareable");
        }

        @Test
        @DisplayName("악성 파일이 공유 가능하면 예외가 발생한다")
        void should_ThrowException_When_MaliciousFileWithShareEnabled() {
            // given
            Visibility visibility = Visibility.PRIVATE;
            DownloadPolicy downloadPolicy = DownloadPolicy.DISABLED;
            SharePolicy sharePolicy = SharePolicy.READ_ONLY;
            ScanStatus scanStatus = ScanStatus.MALICIOUS;
            Encryption encryption = Encryption.NONE;

            // when & then
            assertThatThrownBy(() -> createFileSecurity(
                visibility,
                downloadPolicy,
                sharePolicy,
                scanStatus,
                encryption
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Malicious file cannot be downloadable or shareable");
        }

        @Test
        @DisplayName("PUBLIC 파일이 다운로드 불가능하면 예외가 발생한다")
        void should_ThrowException_When_PublicFileWithDownloadDisabled() {
            // given
            Visibility visibility = Visibility.PUBLIC;
            DownloadPolicy downloadPolicy = DownloadPolicy.DISABLED;
            SharePolicy sharePolicy = SharePolicy.READ_ONLY;
            ScanStatus scanStatus = ScanStatus.CLEAN;
            Encryption encryption = Encryption.NONE;

            // when & then
            assertThatThrownBy(() -> createFileSecurity(
                visibility,
                downloadPolicy,
                sharePolicy,
                scanStatus,
                encryption
            ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Public file must be downloadable");
        }
    }

    @Nested
    @DisplayName("equals & hashCode")
    class EqualsAndHashCodeTest {

        @Test
        @DisplayName("같은 값을 가진 FileSecurity는 동등하다")
        void should_BeEqual_When_SameValue() {
            // given
            Visibility visibility = Visibility.PRIVATE;
            DownloadPolicy downloadPolicy = DownloadPolicy.ALLOWED;
            SharePolicy sharePolicy = SharePolicy.DISABLED;
            ScanStatus scanStatus = ScanStatus.CLEAN;
            Encryption encryption = Encryption.NONE;

            FileSecurity fileSecurity1 = createFileSecurity(
                visibility,
                downloadPolicy,
                sharePolicy,
                scanStatus,
                encryption
            );

            FileSecurity fileSecurity2 = createFileSecurity(
                visibility,
                downloadPolicy,
                sharePolicy,
                scanStatus,
                encryption
            );

            // when & then
            assertThat(fileSecurity1).isEqualTo(fileSecurity2);
            assertThat(fileSecurity1.hashCode()).isEqualTo(fileSecurity2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 FileSecurity는 동등하지 않다")
        void should_NotBeEqual_When_DifferentValue() {
            // given
            FileSecurity fileSecurity1 = createFileSecurity(
                Visibility.PRIVATE,
                DownloadPolicy.ALLOWED,
                SharePolicy.DISABLED,
                ScanStatus.CLEAN,
                Encryption.NONE
            );

            FileSecurity fileSecurity2 = createFileSecurity(
                Visibility.PUBLIC,
                DownloadPolicy.ALLOWED,
                SharePolicy.DISABLED,
                ScanStatus.CLEAN,
                Encryption.NONE
            );

            // when & then
            assertThat(fileSecurity1).isNotEqualTo(fileSecurity2);
        }
    }
}

