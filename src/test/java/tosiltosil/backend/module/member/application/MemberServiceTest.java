package tosiltosil.backend.module.member.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import tosiltosil.backend.common.domain.exception.ConflictException;
import tosiltosil.backend.module.member.domain.LocalAccountRepository;
import tosiltosil.backend.module.member.domain.value.LoginType;
import tosiltosil.backend.module.member.infrastructure.MemberJpaRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberJpaRepository memberJpaRepository;

    @Mock
    private LocalAccountRepository localAccountRepository;

    @Test
    void 중복_이메일_예외처리() {
        //given
        String email = "duplicate@example.com";
        LoginType local = LoginType.LOCAL;

        when(memberJpaRepository.findByEmailAndLoginType(email, local))
                .thenReturn(true);

        // when & then
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> memberService.validateEmail(email, local));

        assertEquals("이미 등록된 이메일입니다.", exception.getMessage());
    }

    @Test
    void 랜덤_코드_생성_성공() {
        // given
        when(memberJpaRepository.existsByCode(anyString()))
                .thenReturn(false);

        // when
        String code = memberService.generateRandomCode();

        // then
        assertEquals(6, code.length());

        verify(memberJpaRepository).existsByCode(code);
    }

    @Test
    void 중복_코드로_랜덤_코드_재생성() {
        // given
        when(memberJpaRepository.existsByCode(anyString()))
                .thenReturn(true)
                .thenReturn(false);

        // when
        String code = memberService.generateRandomCode();

        // then
        assertEquals(6, code.length());

        verify(memberJpaRepository, times(2)).existsByCode(anyString());
    }
}
