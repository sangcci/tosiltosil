package tosiltosil.backend.module.email.application;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tosiltosil.backend.common.domain.exception.BadRequestException;
import tosiltosil.backend.common.domain.exception.ConflictException;
import tosiltosil.backend.common.domain.exception.InvalidEmailCodeException;
import tosiltosil.backend.common.domain.exception.NotFoundException;
import tosiltosil.backend.module.auth.infrastructure.TemporaryTokenRedisRepository;
import tosiltosil.backend.module.email.domain.EmailAuthMeta;
import tosiltosil.backend.module.email.domain.request.EmailAuthRequest;
import tosiltosil.backend.module.email.domain.request.EmailSendRequest;
import tosiltosil.backend.module.email.domain.response.EmailAuthResponse;
import tosiltosil.backend.module.email.infrastructure.AuthNumberRedisRepository;
import tosiltosil.backend.module.email.infrastructure.EmailAuthRedisRepository;
import tosiltosil.backend.module.member.application.MemberService;
import tosiltosil.backend.support.IntegrationTestSupport;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static tosiltosil.backend.module.email.domain.value.EmailAuthPurpose.FORGOT_PASSWORD;
import static tosiltosil.backend.module.email.domain.value.EmailAuthPurpose.SIGN_UP;

@SuppressWarnings("NonAsciiCharacters")
class EmailServiceTest extends IntegrationTestSupport {

    @Autowired
    private EmailService emailService;

    @MockitoBean
    private MemberService memberService;

    @Autowired
    private EmailAuthRedisRepository emailAuthRedisRepository;

    @Autowired
    private AuthNumberRedisRepository authNumberRedisRepository;

    @Autowired
    private TemporaryTokenRedisRepository temporaryTokenRedisRepository;

    @Value("${email.auth.max-send-count}")
    private int maxSendCount;

    @Value("${email.auth.max-attempt-count}")
    private int maxAuthCount;

    private String email;

    @BeforeEach
    void setUp() {
        email = "test@example.com";
    }

    @AfterEach
    void tearDown() {
        emailAuthRedisRepository.delete(email);
        authNumberRedisRepository.delete(email);
        temporaryTokenRedisRepository.delete(email);
    }

    @Test
    void 이메일_전송_시도_횟수_초과로_이메일_전송_실패() {
        // given
        EmailSendRequest request = new EmailSendRequest(email, SIGN_UP.name());

        int currentAuthCount = 1;
        emailAuthRedisRepository.save(email, maxSendCount, currentAuthCount, 300L);

        // when & then
        assertThatThrownBy(() -> emailService.sendAuthEmail(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("일일 이메일 인증 횟수를 초과하였습니다.");
    }

    @Test
    void 이메일_인증_시도_횟수_초과로_이메일_전송_실패() {
        // given
        EmailSendRequest request = new EmailSendRequest(email, SIGN_UP.name());

        int currentSendCount = 1;
        emailAuthRedisRepository.save(email, currentSendCount, maxAuthCount, 300L);

        // when & then
        assertThatThrownBy(() -> emailService.sendAuthEmail(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("일일 이메일 인증 횟수를 초과하였습니다.");
    }

    @Test
    void 이미_가입된_이메일로_회원가입용_이메일_인증_시도하여_전송_실패() {
        // given
        String duplicatedEmail = "test@example.com";
        EmailSendRequest request = new EmailSendRequest(duplicatedEmail, SIGN_UP.name());

        doThrow(new ConflictException("이미 등록된 이메일입니다."))
                .when(memberService).validateEmailIsExist(anyString(), anyString());

        // when & then
        assertThatThrownBy(() -> emailService.sendAuthEmail(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("이미 등록된 이메일입니다.");
    }

    @Test
    void 가입되지_않은_이메일로_비밀번호_찾기용_이메일_인증_시도하여_전송_실패() {
        // given
        EmailSendRequest request = new EmailSendRequest(email, FORGOT_PASSWORD.name());
        doThrow(new BadRequestException("등록되지 않은 이메일입니다."))
                .when(memberService).validateEmailIsExistForPasswordReset(anyString(), anyString());

        // when & then
        assertThatThrownBy(() -> emailService.sendAuthEmail(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("등록되지 않은 이메일입니다.");
    }

    @Test
    void 인증번호_검증_성공() {
        // given
        String authNumber = "123456";
        EmailAuthRequest request = new EmailAuthRequest(email, authNumber);

        int currentSendCount = 1;
        int currentAuthCount = 0;
        emailAuthRedisRepository.save(email, currentSendCount, currentAuthCount, 300L);
        authNumberRedisRepository.save(email, authNumber, 300L);

        // when
        EmailAuthResponse response = emailService.verifyAuthEmail(request);

        // then
        assertThat(response.temporaryToken()).isNotBlank();
        Optional<String> afterVerification = authNumberRedisRepository.get(email);
        assertThat(afterVerification).isEmpty();
    }

    @Test
    void 잘못된_인증번호로_검증_실패() {
        // given
        String authNumber = "123456";
        String wrongAuthNumber = "654321";
        EmailAuthRequest request = new EmailAuthRequest(email, wrongAuthNumber);

        int currentSendCount = 1;
        int currentAuthCount = 0;

        emailAuthRedisRepository.save(email, currentSendCount, currentAuthCount, 300L);
        authNumberRedisRepository.save(email, authNumber, 300L);

        // when & then
        assertThatThrownBy(() -> emailService.verifyAuthEmail(request))
                .isInstanceOf(InvalidEmailCodeException.class)
                .hasMessage("금일 총 1회 틀렸습니다. 하루 최대 5회까지 가능합니다.");

        EmailAuthMeta authMeta = emailAuthRedisRepository.get(email);
        assertThat(authMeta.authFailCount()).isEqualTo(1);
    }

    @Test
    void 인증번호_시도_횟수_초과로_인증_실패() {
        // given
        String authNumber = "123456";
        EmailAuthRequest request = new EmailAuthRequest(email, authNumber);

        int currentSendCount = 1;
        emailAuthRedisRepository.save(email, currentSendCount, maxAuthCount, 300L);

        // when & then
        assertThatThrownBy(() -> emailService.verifyAuthEmail(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("일일 이메일 인증 횟수를 초과하였습니다.");
    }

    @Test
    void 인증번호_유효_시간_만료_및_Redis_데이터가_존재하지않아_검증_실패() {
        // given
        String authNumber = "123456";

        // 인증번호 이메일을 전송했음을 나타냄
        emailAuthRedisRepository.save(email, 1, 0, 100L);

        EmailAuthRequest request = new EmailAuthRequest(email, authNumber);

        // when & then
        assertThat(authNumberRedisRepository.get(email)).isEmpty();

        assertThatThrownBy(() -> emailService.verifyAuthEmail(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("인증 유효 시간이 만료되었거나, 잘못된 인증 요청입니다.");
    }

    @Test
    void 인증_번호_전송하지_않은_이메일로_인증번호_시도_시_실패() {
        // given
        String authNumber = "123456";
        EmailAuthRequest request = new EmailAuthRequest(email, authNumber);

        // when & then
        assertThat(authNumberRedisRepository.get(email)).isEmpty();

        assertThatThrownBy(() -> emailService.verifyAuthEmail(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("이메일 인증 요청을 먼저 진행해야 합니다.");
    }
}
