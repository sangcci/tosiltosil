package tosiltosil.backend.module.terms.domain.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tosiltosil.backend.common.domain.exception.BadRequestException;
import tosiltosil.backend.common.domain.exception.NotFoundException;
import tosiltosil.backend.module.terms.domain.TermsRepository;
import tosiltosil.backend.module.terms.domain.request.TermsDetail;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TermsValidatorTest {

    @InjectMocks
    private TermsValidator termsValidator;

    @Mock
    private TermsRepository termsRepository;

    @Test
    void 정상적인_약관_검증_성공() {
        //given
        List<String> termsTitle = List.of("terms1", "terms2");
        when(termsRepository.findTitleList()).thenReturn(termsTitle);

        List<TermsDetail> validTerms = List.of(
                new TermsDetail("terms1", "0.1.0", true),
                new TermsDetail("terms2", "0.1.0", true)
        );

        when(termsRepository.findVersionId("terms1", "0.1.0")).thenReturn(Optional.of(1L));
        when(termsRepository.findVersionId("terms2", "0.1.0")).thenReturn(Optional.of(2L));

        when(termsRepository.findLastVersion("terms1")).thenReturn(Optional.of("0.1.0"));
        when(termsRepository.findLastVersion("terms2")).thenReturn(Optional.of("0.1.0"));

        when(termsRepository.findTermsIsRequired("terms1", "0.1.0")).thenReturn(Optional.of(true));
        when(termsRepository.findTermsIsRequired("terms2", "0.1.0")).thenReturn(Optional.of(true));

        // when & then
        assertDoesNotThrow(() -> termsValidator.validateTerms(validTerms));
    }

    @Test
    void 중복된_약관_예외처리() {
        //given
        List<String> termsTitle = List.of("terms1", "terms2");
        when(termsRepository.findTitleList()).thenReturn(termsTitle);

        List<TermsDetail> terms = List.of(
                new TermsDetail("terms1", "0.1.0", true),
                new TermsDetail("terms1", "0.1.0", true)
        );

        // when & then
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> termsValidator.validateTerms(terms)
        );

        assertEquals("중복된 약관 동의가 존재합니다.", exception.getMessage());
    }

    @Test
    void 존재하지_않는_약관_예외처리() {
        //given
        List<String> termsTitle = List.of("terms");
        when(termsRepository.findTitleList()).thenReturn(termsTitle);

        List<TermsDetail> terms = List.of(
                new TermsDetail("잘못된_약관", "0.1.0", true)
        );

        when(termsRepository.findVersionId("잘못된_약관", "0.1.0")).thenReturn(Optional.empty());

        // when & then
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> termsValidator.validateTerms(terms)
        );

        assertEquals("약관을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void 최신_약관이_아닌_경우_예외처리() {
        //given
        List<String> termsTitle = List.of("terms1");
        when(termsRepository.findTitleList()).thenReturn(termsTitle);

        List<TermsDetail> terms = List.of(
                new TermsDetail("terms1", "0.1.0", true)
        );

        when(termsRepository.findVersionId(anyString(), anyString())).thenReturn(Optional.of(1L));
        when(termsRepository.findLastVersion("terms1")).thenReturn(Optional.of("0.3.0"));

        // when & then
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> termsValidator.validateTerms(terms)
        );

        assertEquals("최신 버전의 약관이 아닙니다.", exception.getMessage());
    }

    @Test
    void 필수_약관_미동의_예외처리() {
        //given
        List<String> termsTitle = List.of("terms1");
        when(termsRepository.findTitleList()).thenReturn(termsTitle);

        List<TermsDetail> terms = List.of(
                new TermsDetail("terms1", "0.1.0", false)
        );

        when(termsRepository.findVersionId(anyString(), anyString())).thenReturn(Optional.of(1L));
        when(termsRepository.findLastVersion(anyString())).thenReturn(Optional.of("0.1.0"));
        when(termsRepository.findTermsIsRequired(anyString(), anyString())).thenReturn(Optional.of(true));

        // when & then
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> termsValidator.validateTerms(terms)
        );

        assertEquals("필수 약관을 동의하지 않았습니다.", exception.getMessage());
    }
}
