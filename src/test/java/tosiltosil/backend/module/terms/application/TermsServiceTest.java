package tosiltosil.backend.module.terms.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import tosiltosil.backend.common.domain.exception.BadRequestException;
import tosiltosil.backend.common.domain.exception.NotFoundException;
import tosiltosil.backend.module.terms.domain.TermsRepository;
import tosiltosil.backend.module.terms.domain.request.TermsDetail;
import tosiltosil.backend.module.terms.domain.validator.TermsValidator;
import tosiltosil.backend.module.terms.infrastructure.MemberTermsJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
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
    void 정상_약관_검증_성공() {
        // given
        List<TermsDetail> terms = createValidTerms();

        doNothing().when(termsValidator).validateTerms(terms);

        // when & then
        assertDoesNotThrow(() -> termsService.validateTerms(terms));

        verify(termsValidator).validateTerms(terms);
    }

    @Test
    void 약관_저장_성공() {
        // given
        UUID memberId = UUID.randomUUID();
        List<TermsDetail> terms = createValidTerms();

        terms.forEach(t -> {
            when(termsRepository.findVersionId(t.title(), t.version()))
                    .thenReturn(Optional.of(1L));
                }
        );

        // when
        termsService.saveTerms(memberId, terms);

        // then
        verify(memberTermsJpaRepository, times(terms.size())).save(any());
    }

    @Test
    void 필수_약관_미동의_예외처리() {
        //given
        List<TermsDetail> terms = List.of(
                new TermsDetail("termsOfService", "0.1.0", false),
                new TermsDetail("privacyPolicy", "0.1.0", true),
                new TermsDetail("ageConfirmation", "0.1.0", true)
        );

        doThrow(new BadRequestException("필수 약관에 대해 동의하지 않았습니다."))
                .when(termsValidator).validateTerms(terms);

        // when & then
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> termsService.validateTerms(terms)
        );

        assertEquals("필수 약관에 대해 동의하지 않았습니다.", exception.getMessage());
        verify(termsValidator).validateTerms(terms);
    }

    @Test
    void 약관_정보가_없을_경우_예외처리() {
        //given
        List<TermsDetail> terms = List.of(
                new TermsDetail("잘못된_약관", "0.0.1", true),
                new TermsDetail("privacyPolicy", "0.1.0", true),
                new TermsDetail("ageConfirmation", "0.1.0", true)
        );

        doThrow(new NotFoundException("약관을 찾을 수 없습니다."))
                .when(termsValidator).validateTerms(terms);

        // when & then
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> termsService.validateTerms(terms)
        );

        assertEquals("약관을 찾을 수 없습니다.", exception.getMessage());
        verify(termsValidator).validateTerms(terms);
    }
}
