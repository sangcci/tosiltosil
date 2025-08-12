package tosiltosil.backend.module.email.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tosiltosil.backend.common.auth.JwtTokenProvider;
import tosiltosil.backend.common.domain.exception.BadRequestException;
import tosiltosil.backend.common.domain.exception.InvalidEmailCodeException;
import tosiltosil.backend.common.domain.exception.NotFoundException;
import tosiltosil.backend.common.util.RandomUtils;
import tosiltosil.backend.module.email.domain.EmailAuthMeta;
import tosiltosil.backend.module.email.domain.request.EmailAuthRequest;
import tosiltosil.backend.module.email.domain.request.EmailSendRequest;
import tosiltosil.backend.module.email.domain.response.EmailAuthResponse;
import tosiltosil.backend.module.email.domain.response.EmailSendResponse;
import tosiltosil.backend.module.email.domain.value.EmailAuthPurpose;
import tosiltosil.backend.module.email.infrastructure.AuthNumberRedisRepository;
import tosiltosil.backend.module.email.infrastructure.EmailAuthRedisRepository;
import tosiltosil.backend.module.member.application.MemberService;

import java.nio.charset.StandardCharsets;

import static tosiltosil.backend.module.email.domain.value.EmailAuthPurpose.FORGOT_PASSWORD;
import static tosiltosil.backend.module.member.domain.value.LoginType.LOCAL;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final AuthNumberRedisRepository authNumberRedisRepository;
    private final EmailAuthRedisRepository emailAuthRedisRepository;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JavaMailSender mailSender;

    private static final int INITIAL_SEND_COUNT = 0;
    private static final int INITIAL_FAIL_COUNT = 0;
    private static final int CODE_LENGTH = 6;
    private static final String EMAIL_TITLE = "토실토실 인증번호";

    @Value("${email.auth.redis-expiration}")
    private long emailAuthExpiration;

    @Value("${email.auth-number.redis-expiration}")
    private long authNumberExpiration;

    @Value("${email.auth.max-send-count}")
    private int maxSendCount;

    @Value("${email.auth.max-attempt-count}")
    private int maxAuthAttemptCount;

    @Value("${spring.mail.username}")
    private String id;

    @Async("emailAsyncExecutor")
    public void sendEmail(String email, String subject, String content) {
        MimeMessagePreparator messagePreparator =
                mimeMessage -> {
                    final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");

                    messageHelper.setFrom(id);
                    messageHelper.setTo(email);
                    messageHelper.setSubject(subject);
                    messageHelper.setText(content, true);
                };
        try {
            mailSender.send(messagePreparator);
        } catch(Exception e) {
            throw new RuntimeException("이메일 전송에 실패하였습니다.", e);
        }
    }

    public EmailSendResponse sendAuthEmail(
            final EmailSendRequest request
    ) {
        String email = request.email();
        EmailAuthPurpose purpose = EmailAuthPurpose.valueOf(request.purpose());

        initEmailAttemptsIfAbsent(email);

        validateCanSendEmail(email);

        validateEmailIsExistByPurpose(email, purpose);

        String authNumber = generateAndSaveAuthNumber(email);

        String content = loadAuthTemplate(authNumber);
        sendEmail(email, EMAIL_TITLE, content);

        increaseSendCount(email);

        return EmailSendResponse.of(email);
    }

    public EmailAuthResponse verifyAuthEmail(
            final EmailAuthRequest request
    ) {
        String email = request.email();

        validateIsSentEmail(email);

        int authFailCount = validateAndGetAuthFailCount(email);

        validateAuthNumber(email, request.authNumber(), authFailCount);

        deleteAuthNumber(email);

        return generateTemporaryToken(email);
    }

    private String loadAuthTemplate(String authNumber) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/email-auth-template.html");
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return template.replace("{{authNumber}}", authNumber);
        } catch (Exception e) {
            throw new RuntimeException("템플릿을 가져올 수 없습니다.", e);
        }
    }

    private void generateEmailAttemptsRedisData(String email) {
        emailAuthRedisRepository.save(email, INITIAL_SEND_COUNT, INITIAL_FAIL_COUNT, emailAuthExpiration);
    }

    private void initEmailAttemptsIfAbsent(String email) {
        if (emailAuthRedisRepository.get(email) == null) {
            generateEmailAttemptsRedisData(email);
        }
    }

    private void validateSendCount(EmailAuthMeta emailAuthMeta) {
        if (emailAuthMeta.sendCount() >= maxSendCount) {
            throw new BadRequestException("일일 이메일 인증 횟수를 초과하였습니다.");
        }
    }

    private void validateAuthFailCount(EmailAuthMeta emailAuthMeta) {
        if (emailAuthMeta.authFailCount() >= maxAuthAttemptCount) {
            throw new BadRequestException("일일 이메일 인증 횟수를 초과하였습니다.");
        }
    }

    private void validateCanSendEmail(String email) {
        EmailAuthMeta emailAuthMeta = getEmailAuthMeta(email);

        validateSendCount(emailAuthMeta);
        validateAuthFailCount(emailAuthMeta);
    }

    private void increaseSendCount(String email) {
        emailAuthRedisRepository.increaseSendCount(email);
    }

    private String generateAndSaveAuthNumber(String email) {
        String authNumber = RandomUtils.generateRandomNumberString(CODE_LENGTH);
        authNumberRedisRepository.save(email, authNumber, authNumberExpiration);
        return authNumber;
    }

    private void validateIsSentEmail(String email) {
        EmailAuthMeta emailAuthMeta = getEmailAuthMeta(email);

        if (emailAuthMeta == null) {
            throw new NotFoundException("이메일 인증 요청을 먼저 진행해야 합니다.");
        }
    }

    private int validateAndGetAuthFailCount(String email) {
        EmailAuthMeta emailAuthMeta = getEmailAuthMeta(email);

        int authFailCount = emailAuthMeta.authFailCount();

        if (authFailCount >= maxAuthAttemptCount) {
            throw new BadRequestException("일일 이메일 인증 횟수를 초과하였습니다.");
        }
        return authFailCount;
    }

    private EmailAuthMeta getEmailAuthMeta(String email) {
        return emailAuthRedisRepository.get(email);
    }

    private void validateEmailIsExistByPurpose(String email, EmailAuthPurpose purpose) {
        if (purpose.equals(FORGOT_PASSWORD)) {
            memberService.validateEmailIsExistForPasswordReset(email, LOCAL.name());
        } else {
            memberService.validateEmailIsExist(email, LOCAL.name());
        }
    }

    private void validateAuthNumber(String email, String authNumber, int authFailCount) {
        String savedAuthNumber = authNumberRedisRepository.get(email)
                .orElseThrow(() -> new NotFoundException("인증 유효 시간이 만료되었거나, 잘못된 인증 요청입니다."));

        if (!savedAuthNumber.equals(authNumber)) {
            int increasedAuthFailCount = increaseAuthFailCount(email);
            throw new InvalidEmailCodeException(increasedAuthFailCount);
        }
    }

    private int increaseAuthFailCount(String email) {
        return emailAuthRedisRepository.increaseAuthFailCount(email);
    }

    private void deleteAuthNumber(String email) {
        authNumberRedisRepository.delete(email);
    }

    private EmailAuthResponse generateTemporaryToken(String email) {
        String temporaryToken = jwtTokenProvider.createTemporaryToken(email);
        return EmailAuthResponse.of(temporaryToken);
    }
}
