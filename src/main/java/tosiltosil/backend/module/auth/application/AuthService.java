package tosiltosil.backend.module.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tosiltosil.backend.module.auth.domain.request.CreateLocalMemberRequest;
import tosiltosil.backend.module.member.application.MemberService;
import tosiltosil.backend.module.member.domain.LocalAccount;
import tosiltosil.backend.module.member.domain.Member;
import tosiltosil.backend.module.member.domain.value.LoginType;
import tosiltosil.backend.module.terms.application.TermsService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberService memberService;
    private final TermsService termsService;
    private final PasswordEncoder passwordEncoder;

    /* TODO
     * S3 구현 및 ImageURL 가져오기
     * 인증번호 검증
     */

    @Transactional
    public void localSignUp(
            final CreateLocalMemberRequest request,
            final MultipartFile profileImage
    ) {
        termsService.validateTerms(request.terms());
        memberService.validateEmail(request.email(), LoginType.LOCAL);

        String code = memberService.generateRandomCode();
        String profileImgUrl = "https://example.com/profile.png"; // S3 구현 후 수정
        Member member = request.toMemberEntities(code, profileImgUrl);
        memberService.saveMember(member);

        String encryptedPassword = passwordEncoder.encode(request.password());
        LocalAccount localAccount = request.toLocalAccountEntities(member.getId(), encryptedPassword);
        memberService.saveLocalAccount(localAccount);

        termsService.saveTerms(member.getId(), request.terms());
    }
}
