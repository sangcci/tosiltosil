package tosiltosil.backend.module.auth.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import tosiltosil.backend.common.domain.exception.ConflictException;
import tosiltosil.backend.module.auth.domain.request.CreateLocalMemberRequest;
import tosiltosil.backend.module.member.application.MemberService;
import tosiltosil.backend.module.member.domain.value.LoginType;
import tosiltosil.backend.module.terms.application.TermsService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private MemberService memberService;

    @Mock
    private TermsService termsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MultipartFile profileImage;

    @Test
    void 이메일_중복_예외가_발생하면_로직을_중지한다() {
        //given
        CreateLocalMemberRequest request = mock(CreateLocalMemberRequest.class);
        String email = "duplicate@example.com";

        when(request.email()).thenReturn(email);

        doThrow(new ConflictException("이미 등록된 이메일입니다."))
                .when(memberService).validateEmail(email, LoginType.LOCAL);

        // when & then
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> authService.localSignUp(request, profileImage)
        );

        assertEquals("이미 등록된 이메일입니다.", exception.getMessage());
        verify(memberService, never()).generateRandomCode();
        verify(memberService, never()).saveMember(any());
        verify(memberService, never()).saveLocalAccount(any());
        verify(termsService, never()).saveTerms(any(UUID.class), any());
    }
}
