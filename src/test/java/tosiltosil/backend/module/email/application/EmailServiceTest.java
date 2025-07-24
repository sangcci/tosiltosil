package tosiltosil.backend.module.email.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tosiltosil.backend.module.email.domain.request.EmailAuthRequest;
import tosiltosil.backend.module.email.domain.response.EmailAuthResponse;
import tosiltosil.backend.module.email.infrastructure.AuthNumberRedisRepository;
import tosiltosil.backend.module.email.infrastructure.EmailAuthRedisRepository;
import tosiltosil.backend.support.IntegrationTestSupport;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class EmailServiceTest extends IntegrationTestSupport {

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailAuthRedisRepository emailAuthRedisRepository;

    @Autowired
    private AuthNumberRedisRepository authNumberRedisRepository;

    @Test
    void 인증번호_검증_성공() {
        // given
        String email = "verify@example.com";
        String authNumber = "123456";
        EmailAuthRequest request = new EmailAuthRequest(email, authNumber);

        emailAuthRedisRepository.save(email, 1, 0, 300L);
        authNumberRedisRepository.save(email, authNumber, 300L);

        // when
        EmailAuthResponse response = emailService.verifyAuthEmail(request);

        // then
        assertThat(response.temporaryToken()).isNotBlank();
        Optional<String> afterVerification = authNumberRedisRepository.get(email);
        assertThat(afterVerification).isEmpty();
    }
}
