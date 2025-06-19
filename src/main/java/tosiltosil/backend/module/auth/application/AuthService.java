package tosiltosil.backend.module.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tosiltosil.backend.common.domain.exception.BadRequestException;
import tosiltosil.backend.common.domain.exception.ConflictException;
import tosiltosil.backend.common.domain.exception.NotFoundException;
import tosiltosil.backend.common.util.RandomUtils;
import tosiltosil.backend.module.auth.domain.request.CreateLocalMemberRequest;
import tosiltosil.backend.module.member.domain.LocalAccount;
import tosiltosil.backend.module.member.domain.LocalAccountRepository;
import tosiltosil.backend.module.member.domain.Member;
import tosiltosil.backend.module.member.domain.value.LoginType;
import tosiltosil.backend.module.member.infrastructure.MemberJpaRepository;
import tosiltosil.backend.module.terms.domain.MemberTerms;
import tosiltosil.backend.module.terms.domain.Terms;
import tosiltosil.backend.module.terms.domain.request.TermsDetail;
import tosiltosil.backend.module.terms.infrastructure.MemberTermsJpaRepository;
import tosiltosil.backend.module.terms.infrastructure.TermsJpaRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberJpaRepository memberJpaRepository;
    private final LocalAccountRepository localAccountRepository;
    private final MemberTermsJpaRepository memberTermsJpaRepository;
    private final TermsJpaRepository termsJpaRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int CODE_LENGTH = 6;
    private static final boolean IS_UPPER_CASE = true;

    /* TODO
     * S3 구현 및 ImageURL 가져오기
     * 인증번호 검증
     */

    @Transactional
    public void localSignUp(
            final CreateLocalMemberRequest request,
            final MultipartFile profileImage
    ) {
        validateTerms(request.terms());
        validateEmail(request.email(), LoginType.LOCAL);

        String code = generateRandomCode();
        String profileImgUrl = "https://example.com/profile.png"; // S3 구현 후 수정
        String encryptedPassword = passwordEncoder.encode(request.password());

        Member member = request.toMemberEntities(code, profileImgUrl);
        memberJpaRepository.save(member);

        LocalAccount localAccount = request.toLocalAccountEntities(member.getId(), encryptedPassword);
        localAccountRepository.save(localAccount);

        saveTerms(member.getId(), request.terms());
    }

    private void validateEmail(String email, LoginType loginType) {
        if (memberJpaRepository.findByEmailAndLoginType(email, loginType))
            throw new ConflictException("이미 등록된 이메일입니다.");
    }

    private void validateTerms(
            final List<TermsDetail> termsDetails
    ) {
        termsDetails.forEach(terms -> {
            if (terms.required() && !terms.agreed()) {
                throw new BadRequestException("필수 동의 약관에 대해 동의하지 않았습니다.");
            }
        });
    }

    private Long getTermsId(final TermsDetail termsDetail) {
        Terms terms = termsJpaRepository.findByTitleAndVersion(termsDetail.title(), termsDetail.version())
                .orElseThrow(() -> new NotFoundException("약관을 찾을 수 없습니다."));
        return terms.getId();
    }

    private void saveTerms(
            final UUID memberId,
            final List<TermsDetail> termsDetails
    ) {
        termsDetails.forEach(terms -> {
            MemberTerms memberTerms = terms.toEntities(memberId, getTermsId(terms));
            memberTermsJpaRepository.save(memberTerms);
        });
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
