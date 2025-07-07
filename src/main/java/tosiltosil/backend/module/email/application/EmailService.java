package tosiltosil.backend.module.email.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

import java.util.UUID;

import static tosiltosil.backend.module.email.domain.value.EmailAuthPurpose.SIGN_UP;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final AuthNumberRedisRepository authNumberRedisRepository;
    private final EmailAuthRedisRepository emailAuthRedisRepository;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    private static final int INITIAL_SEND_COUNT = 1;
    private static final int INITIAL_FAIL_COUNT = 0;
    private static final int CODE_LENGTH = 6;

    @Value("${email.auth.redis-expiration}")
    private long emailAuthExpiration;

    @Value("${email.auth-number.redis-expiration}")
    private long authNumberExpiration;

    @Value("${email.auth.max-send-count}")
    private int maxSendCount;

    @Value("${email.auth.max-attempt-count}")
    private int maxAuthAttemptCount;

    public EmailSendResponse sendEmail(
            final UUID clientId,
            final EmailSendRequest request
    ) {
        UUID currentClientId =
                clientId == null ? generateAndSaveNewClientId() : clientId;

        validateSendCount(currentClientId);
        EmailAuthPurpose purpose = EmailAuthPurpose.valueOf(request.purpose());

        if (purpose.equals(SIGN_UP)) {
            validateDuplicatedEmail(request.email());
        }

        String authNumber = generateAndSaveAuthNumber(request.email());

        // TODO : 이메일 전송

        return EmailSendResponse.of(request.email(), currentClientId);
    }

    public EmailAuthResponse verifyEmailAuth(
            final UUID clientId,
            final EmailAuthRequest request
    ) {
        int authFailCount = validateAndGetAuthFailCount(clientId);

        validateAuthNumber(clientId, request.email(), request.authNumber(), authFailCount);

        deleteAuthNumber(request.email());

        return generateTemporaryToken(request.email());
    }

    private UUID generateAndSaveNewClientId() {
        UUID newClientId = UUID.randomUUID();
        emailAuthRedisRepository.save(newClientId, INITIAL_SEND_COUNT, INITIAL_FAIL_COUNT, emailAuthExpiration);
        return newClientId;
    }

    private void validateSendCount(UUID clientId) {
        EmailAuthMeta emailAuthMeta = getEmailAuthMeta(clientId);

        if (emailAuthMeta.sendCount() > maxSendCount) {
            throw new BadRequestException("일일 전송 제한 횟수를 초과하였습니다.");
        }

        emailAuthRedisRepository.increaseSendCount(clientId);
    }

    private String generateAndSaveAuthNumber(String email) {
        String authNumber = RandomUtils.generateRandomNumberString(CODE_LENGTH);
        authNumberRedisRepository.save(email, authNumber, authNumberExpiration);
        return authNumber;
    }

    private int validateAndGetAuthFailCount(UUID clientId) {
        EmailAuthMeta emailAuthMeta = getEmailAuthMeta(clientId);

        int authFailCount = emailAuthMeta.authFailCount();

        if (authFailCount >= maxAuthAttemptCount) {
            throw new BadRequestException("일일 이메일 인증 횟수를 초과하였습니다.");
        }
        return authFailCount;
    }

    private EmailAuthMeta getEmailAuthMeta(UUID clientId) {
        return emailAuthRedisRepository.get(clientId);
    }

    private void validateDuplicatedEmail(String email) {
        memberService.validateEmailNotDuplicated(email, "LOCAL");
    }

    private void validateAuthNumber(UUID clientId, String email, String authNumber, int authFailCount) {
        String savedAuthNumber = authNumberRedisRepository.get(email)
                .orElseThrow(() -> new NotFoundException("인증번호 데이터를 찾을 수 없습니다."));

        if (!savedAuthNumber.equals(authNumber)) {
            increaseAuthFailCount(clientId);
            throw new InvalidEmailCodeException(++authFailCount);
        }
    }

    private void increaseAuthFailCount(UUID clientId) {
        emailAuthRedisRepository.increaseAuthFailCount(clientId);
    }

    private void deleteAuthNumber(String email) {
        authNumberRedisRepository.delete(email);
    }

    private EmailAuthResponse generateTemporaryToken(String email) {
        String temporaryToken = jwtTokenProvider.createTemporaryToken(email);
        return EmailAuthResponse.of(temporaryToken);
    }
}
