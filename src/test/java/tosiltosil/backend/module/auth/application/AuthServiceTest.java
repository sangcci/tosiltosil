package tosiltosil.backend.module.auth.application;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import tosiltosil.backend.module.member.application.MemberService;
import tosiltosil.backend.module.terms.application.TermsService;

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

}
