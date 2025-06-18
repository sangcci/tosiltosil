package tosiltosil.backend.module.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tosiltosil.backend.common.auth.domain.LocalAccount;
import tosiltosil.backend.common.auth.domain.LocalAccountRepository;
import tosiltosil.backend.common.util.RandomUtils;
import tosiltosil.backend.module.auth.domain.request.CreateLocalMemberRequest;
import tosiltosil.backend.module.member.domain.Member;
import tosiltosil.backend.module.member.infrastructure.MemberJpaRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberJpaRepository memberJpaRepository;
    private final LocalAccountRepository localAccountRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int CODE_LENGTH = 6;
    private static final boolean IS_UPPER_CASE = true;

    /* TODO
     * 약관동의 유효성 검증
     * S3 구현 및 ImageURL 가져오기
     * 인증번호 검증
     */

    @Transactional
    public void localSignUp(
            final CreateLocalMemberRequest request,
            MultipartFile profileImage
    ) {
        String code = generateRandomCode();
        String profileImgUrl = "https://example.com/profile.png"; // S3 구현 후 수정
        String encryptedPassword = passwordEncoder.encode(request.password());

        Member member = request.toMemberEntities(code, profileImgUrl);
        memberJpaRepository.save(member);

        LocalAccount localAccount = request.toLocalAccountEntities(member.getId(), encryptedPassword);
        localAccountRepository.save(localAccount);
    }

    private String generateRandomCode() {
        String code;

        do {
            code = RandomUtils.generateRandomMixString(CODE_LENGTH, IS_UPPER_CASE);
        } while (isCodeExist(code));

        return code;
    }

    private boolean isCodeExist(final String code) {
        return memberJpaRepository.existsByCode(code);
    }
}
