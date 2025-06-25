package tosiltosil.backend.module.terms.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tosiltosil.backend.common.domain.exception.BadRequestException;
import tosiltosil.backend.module.terms.domain.TermsRepository;
import tosiltosil.backend.module.terms.domain.request.TermsDetail;
import tosiltosil.backend.module.terms.domain.validator.TermsValidator;
import tosiltosil.backend.module.terms.infrastructure.MemberTermsJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TermsServiceTest {

    @InjectMocks
    private TermsService termsService;

    @Mock
    private TermsRepository termsRepository;

    @Mock
    private MemberTermsJpaRepository memberTermsJpaRepository;

    @Mock
    private TermsValidator termsValidator;

    private List<TermsDetail> createValidTerms() {
        return List.of(
                new TermsDetail("termsOfService", "0.1.0", true),
                new TermsDetail("privacyPolicy", "0.1.0", true),
                new TermsDetail("ageConfirmation", "0.1.0", true)
        );
    }

    @Test
    void 정상적인_약관_검증_성공() {
        // given
        List<TermsDetail> validTerms = createValidTerms();

        doNothing().when(termsValidator).validateTerms(validTerms);

        // when
        Throwable thrown = catchThrowable(() -> termsService.validateTerms(validTerms));

        // then
        assertThat(thrown).isNull();
        verify(termsValidator).validateTerms(validTerms);
    }

    @Test
    void 필수_미동의_약관_예외처리() {
        //given
        List<TermsDetail> invalidTerms = List.of(
                new TermsDetail("termsOfService", "0.1.0", false),
                new TermsDetail("privacyPolicy", "0.1.0", true),
                new TermsDetail("ageConfirmation", "0.1.0", true)
        );

        doThrow(new BadRequestException("필수 약관에 대해 동의하지 않았습니다."))
                .when(termsValidator).validateTerms(invalidTerms);

        // when
        Throwable thrown = catchThrowable(() -> termsService.validateTerms(invalidTerms));

        // then
        assertThat(thrown)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("필수 약관에 대해 동의하지 않았습니다.");

        verify(termsValidator).validateTerms(invalidTerms);
    }

    @Test
    void 약관_저장_성공() {
        // given
        UUID memberId = UUID.randomUUID();
        List<TermsDetail> validTerms = createValidTerms();

        validTerms.forEach(t -> {
            when(termsRepository.findVersionId(t.title(), t.version()))
                    .thenReturn(Optional.of(1L));
                }
        );

        // when
        termsService.saveTerms(memberId, validTerms);

        // then
        verify(memberTermsJpaRepository, times(validTerms.size())).save(any());
    }
}
