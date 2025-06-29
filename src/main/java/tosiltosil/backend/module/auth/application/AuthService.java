package tosiltosil.backend.module.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tosiltosil.backend.common.auth.JwtTokenProvider;
import tosiltosil.backend.common.auth.domain.response.TokenPair;
import tosiltosil.backend.common.domain.exception.UnauthorizedException;
import tosiltosil.backend.common.util.CookieUtil;
import tosiltosil.backend.module.auth.domain.request.CreateLocalMemberRequest;
import tosiltosil.backend.module.auth.domain.request.LocalLoginRequest;
import tosiltosil.backend.module.auth.domain.response.CreateLocalMemberResponse;
import tosiltosil.backend.module.auth.domain.response.LocalLoginResponse;
import tosiltosil.backend.module.member.application.MemberService;
import tosiltosil.backend.module.member.domain.LocalAccount;
import tosiltosil.backend.module.member.domain.Member;
import tosiltosil.backend.module.member.domain.value.LoginType;
import tosiltosil.backend.module.terms.application.TermsService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberService memberService;
    private final TermsService termsService;
    private final PasswordEncoder passwordEncoder;
    private final CookieUtil cookieUtil;
    private final JwtTokenProvider jwtTokenProvider;

    /* TODO
     * S3 구현 및 ImageURL 가져오기
     * 인증번호 검증
     */

    @Transactional
    public CreateLocalMemberResponse localSignUp(
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

        return CreateLocalMemberResponse.of(member.getNickname());
    }

    public LocalLoginResponse localLogin(
            final LocalLoginRequest request
    ) {
        Member member = memberService.findByEmail(request.email());
        UUID memberId = member.getId();

        validatePassword(request.password(), memberId);

        TokenPair authTokens = jwtTokenProvider.createTokenPair(memberId);
        HttpHeaders headers = cookieUtil.generateAccessAndRefreshTokenCookies(authTokens.accessToken(), authTokens.refreshToken());

        return LocalLoginResponse.of(memberId, headers);
    }

    private void validatePassword(
            final String password,
            final UUID memberId
    ) {
        String encryptPassword = memberService.getPassword(memberId);

        if (!passwordEncoder.matches(password, encryptPassword))
            throw new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다.");

    }
}
