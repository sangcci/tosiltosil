package tosiltosil.backend.module.email.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tosiltosil.backend.common.domain.exception.BadRequestException;
import tosiltosil.backend.common.util.RandomUtils;
import tosiltosil.backend.module.email.domain.EmailAuthMeta;
import tosiltosil.backend.module.email.domain.request.EmailSendRequest;
import tosiltosil.backend.module.email.domain.response.EmailSendResponse;
import tosiltosil.backend.module.email.infrastructure.AuthNumberRedisRepository;
import tosiltosil.backend.module.email.infrastructure.EmailAuthRedisRepository;
import tosiltosil.backend.module.member.application.MemberService;

import java.util.UUID;

import static tosiltosil.backend.module.email.domain.value.EmailAuthPurpose.SIGN_UP;
import static tosiltosil.backend.module.member.domain.value.LoginType.LOCAL;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final AuthNumberRedisRepository authNumberRedisRepository;
    private final EmailAuthRedisRepository emailAuthRedisRepository;
    private final MemberService memberService;

    private static final int INITIAL_SEND_COUNT = 1;
    private static final int INITIAL_FAIL_COUNT = 0;
    private static final long AUTH_NUMBER_TTL_SECONDS = 300L;
    private static final int MAX_SEND_COUNT = 5;
    private static final int CODE_LENGTH = 6;
    private static final boolean IS_UPPER_CASE = true;

    public EmailSendResponse sendEmail(
            final UUID clientId,
            final EmailSendRequest request
    ) {
        UUID currentClientId =
                clientId == null ? generateAndSaveNewClientId() : clientId;

        validateSendCount(currentClientId);

        if (request.purpose() == SIGN_UP) {
            validateDuplicatedEmail(request.email());
        }

        String authNumber = generateAndSaveAuthNumber(request.email());

        // TODO : 이메일 전송

        return EmailSendResponse.of(request.email(), currentClientId);
    }

    private UUID generateAndSaveNewClientId() {
        UUID newClientId = UUID.randomUUID();
        emailAuthRedisRepository.save(newClientId, INITIAL_SEND_COUNT, INITIAL_FAIL_COUNT);
        return newClientId;
    }

    private void validateSendCount(UUID clientId) {
        EmailAuthMeta emailAuthMeta = emailAuthRedisRepository.get(clientId);

        if (emailAuthMeta.sendCount() >= MAX_SEND_COUNT) {
            throw new BadRequestException("일일 전송 제한 횟수를 초과하였습니다.");
        }

        emailAuthRedisRepository.increaseSendCount(clientId);
    }

    private String generateAndSaveAuthNumber(String email) {
        String authNumber = RandomUtils.generateRandomMixString(CODE_LENGTH, IS_UPPER_CASE);
        authNumberRedisRepository.save(email, authNumber, AUTH_NUMBER_TTL_SECONDS);
        return authNumber;
    }

    private void validateDuplicatedEmail(String email) {
        memberService.validateEmail(email, LOCAL);
    }
}
