package tosiltosil.backend.module.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tosiltosil.backend.common.auth.JwtTokenProvider;
import tosiltosil.backend.common.auth.domain.response.TemporaryTokenInfo;
import tosiltosil.backend.common.auth.domain.response.TokenPair;
import tosiltosil.backend.common.domain.exception.UnauthorizedException;
import tosiltosil.backend.module.auth.domain.request.CreateLocalMemberRequest;
import tosiltosil.backend.module.auth.domain.request.LocalLoginRequest;
import tosiltosil.backend.module.auth.domain.response.CreateLocalMemberResponse;
import tosiltosil.backend.module.auth.domain.response.LocalLoginResponse;
import tosiltosil.backend.module.member.application.MemberService;
import tosiltosil.backend.module.member.domain.LocalAccount;
import tosiltosil.backend.module.member.domain.Member;
import tosiltosil.backend.module.terms.application.TermsService;

import java.util.UUID;

import static tosiltosil.backend.module.member.domain.value.LoginType.LOCAL;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberService memberService;
    private final TermsService termsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /* TODO
     * S3 구현 및 ImageURL 가져오기
     */

    @Transactional
    public CreateLocalMemberResponse localSignUp(
            final String temporaryToken,
            final CreateLocalMemberRequest request,
            final MultipartFile profileImage
    ) {
        String email = getEmailFromRedis(temporaryToken);
        memberService.validateEmailIsExist(email, LOCAL.name());

        termsService.validateTerms(request.terms());

        String code = memberService.generateRandomCode();
        String profileImgUrl = "https://example.com/profile.png"; // S3 구현 후 수정
        Member member = request.toMemberEntities(email, code, profileImgUrl);
        memberService.saveMember(member);

        String encryptedPassword = passwordEncoder.encode(request.password());
        LocalAccount localAccount = request.toLocalAccountEntities(member.getId(), encryptedPassword);
        memberService.saveLocalAccount(localAccount);

        termsService.saveTerms(member.getId(), request.terms());

        deleteTemporaryTokenFromRedis(email);

        return CreateLocalMemberResponse.of(member.getNickname());
    }

    @Transactional
    public LocalLoginResponse localLogin(
            final LocalLoginRequest request
    ) {
        Member member = memberService.findByEmail(request.email());
        UUID memberId = member.getId();

        validatePassword(request.password(), memberId);

        TokenPair authTokens = jwtTokenProvider.createTokens(memberId);
        return LocalLoginResponse.of(memberId, authTokens.accessToken(), authTokens.refreshToken());
    }

    @Transactional
    public TokenPair reissueTokens(String refreshToken) {
        return jwtTokenProvider.reissueTokens(refreshToken);
    }

    private void validatePassword(
            final String password,
            final UUID memberId
    ) {
        String encryptPassword = memberService.findPasswordByMemberId(memberId);

        if (!passwordEncoder.matches(password, encryptPassword))
            throw new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    private String getEmailFromRedis(String temporaryToken) {
        TemporaryTokenInfo temporaryTokenInfo = jwtTokenProvider.retrieveTemporaryToken(temporaryToken);
        return temporaryTokenInfo.email();
    }

    private void deleteTemporaryTokenFromRedis(String email) {
        jwtTokenProvider.deleteTemporaryTokenFromRedis(email);
    }
}
