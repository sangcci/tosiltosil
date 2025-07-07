package tosiltosil.backend.module.member.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import tosiltosil.backend.common.domain.exception.ConflictException;
import tosiltosil.backend.module.member.domain.LocalAccountRepository;
import tosiltosil.backend.module.member.domain.MemberRepository;
import tosiltosil.backend.module.member.domain.value.LoginType;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private LocalAccountRepository localAccountRepository;

    @Test
    void 중복_이메일_예외처리() {
        //given
        String email = "duplicate@example.com";

        when(memberRepository.existsByEmailAndLoginType(email, LoginType.valueOf("LOCAL")))
                .thenReturn(true);

        // when & then
        Throwable thrown = catchThrowable(() -> memberService.validateEmailNotDuplicated(email, "LOCAL"));

        assertThat(thrown)
                .isInstanceOf(ConflictException.class)
                .hasMessage("이미 등록된 이메일입니다.");
    }

    @Test
    void 중복_코드로_랜덤_코드_재생성() {
        // given
        when(memberRepository.existsByCode(anyString()))
                .thenReturn(true)
                .thenReturn(false);

        // when
        String code = memberService.generateRandomCode();

        // then
        assertThat(code).hasSize(6);

        verify(memberRepository, times(2)).existsByCode(anyString());
    }
}
