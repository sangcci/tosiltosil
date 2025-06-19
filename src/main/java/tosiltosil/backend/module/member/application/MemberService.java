package tosiltosil.backend.module.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tosiltosil.backend.common.domain.exception.ConflictException;
import tosiltosil.backend.common.util.RandomUtils;
import tosiltosil.backend.module.member.domain.LocalAccount;
import tosiltosil.backend.module.member.domain.LocalAccountRepository;
import tosiltosil.backend.module.member.domain.Member;
import tosiltosil.backend.module.member.domain.value.LoginType;
import tosiltosil.backend.module.member.infrastructure.MemberJpaRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberJpaRepository memberJpaRepository;
    private final LocalAccountRepository localAccountRepository;

    private static final int CODE_LENGTH = 6;
    private static final boolean IS_UPPER_CASE = true;

    public void saveMember(
            final Member member
    ) {
        memberJpaRepository.save(member);
    }

    public void saveLocalAccount(
            final LocalAccount localAccount
    ) {
        localAccountRepository.save(localAccount);
    }

    public void validateEmail(String email, LoginType loginType) {
        if (memberJpaRepository.findByEmailAndLoginType(email, loginType))
            throw new ConflictException("이미 등록된 이메일입니다.");
    }

    public String generateRandomCode() {
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
