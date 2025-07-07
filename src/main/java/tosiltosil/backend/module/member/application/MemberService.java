package tosiltosil.backend.module.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tosiltosil.backend.common.domain.exception.ConflictException;
import tosiltosil.backend.common.domain.exception.NotFoundException;
import tosiltosil.backend.common.domain.exception.UnauthorizedException;
import tosiltosil.backend.common.util.RandomUtils;
import tosiltosil.backend.module.member.domain.LocalAccount;
import tosiltosil.backend.module.member.domain.LocalAccountRepository;
import tosiltosil.backend.module.member.domain.Member;
import tosiltosil.backend.module.member.domain.MemberRepository;
import tosiltosil.backend.module.member.domain.response.ProfileDto;
import tosiltosil.backend.module.member.domain.value.LoginType;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final LocalAccountRepository localAccountRepository;

    private static final int CODE_LENGTH = 6;
    private static final boolean IS_UPPER_CASE = true;

    public void saveMember(
            final Member member
    ) {
        memberRepository.save(member);
    }

    public void saveLocalAccount(
            final LocalAccount localAccount
    ) {
        localAccountRepository.save(localAccount);
    }

    public String generateRandomCode() {
        String code;

        do {
            code = RandomUtils.generateRandomMixString(CODE_LENGTH, IS_UPPER_CASE);
        } while (isCodeExist(code));

        return code;
    }

    @Transactional(readOnly = true)
    public void validateEmailNotDuplicated(String email, String loginType) {
        LoginType loginTypeEnum = LoginType.valueOf(loginType);
        if (memberRepository.existsByEmailAndLoginType(email, loginTypeEnum))
            throw new ConflictException("이미 등록된 이메일입니다.");
    }

    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다."));
    }

    @Transactional(readOnly = true)
    public String findPasswordByMemberId(UUID memberId) {
        return localAccountRepository.findPasswordByMemberId(memberId)
                .orElseThrow(() -> new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다."));
    }

    @Transactional(readOnly = true)
    public ProfileDto getMyProfile(UUID memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new NotFoundException("찾을 수 없는 회원입니다."));
        return ProfileDto.of(member.getNickname());
    }

    private boolean isCodeExist(final String code) {
        return memberRepository.existsByCode(code);
    }
}
