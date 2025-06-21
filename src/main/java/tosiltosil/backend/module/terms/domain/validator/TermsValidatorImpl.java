package tosiltosil.backend.module.terms.domain.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tosiltosil.backend.common.domain.exception.BadRequestException;
import tosiltosil.backend.common.domain.exception.NotFoundException;
import tosiltosil.backend.module.terms.domain.TermsRepository;
import tosiltosil.backend.module.terms.domain.request.TermsDetail;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class TermsValidatorImpl implements TermsValidator {

    private final TermsRepository termsRepository;

    private static final Set<String> REQUIRED_TERMS = Set.of(
            "termsOfService",
            "privacyPolicy",
            "ageConfirmation"
    );

    @Override
    public void validateTerms(List<TermsDetail> termsDetail) {
        validateDuplicateTerms(termsDetail);

        termsDetail.forEach(terms -> {
            validateRequiredTermsAgreed(terms);
            validateTermsExist(terms);
        });
    }

    /**
     * 중복된 동의 검증
     */
    private void validateDuplicateTerms(List<TermsDetail> termsDetails) {
        long cnt = termsDetails.stream()
                .map(TermsDetail::title)
                .distinct()
                .count();

        if (cnt != REQUIRED_TERMS.size())
            throw new BadRequestException("중복된 약관 동의가 존재합니다.");
    }

    /**
     * 약관 존재 검증
     */
    private void validateTermsExist(TermsDetail terms) {
        termsRepository.findVersionId(terms.title(), terms.version())
                .orElseThrow(() -> new NotFoundException("약관을 찾을 수 없습니다."));
    }

    /**
     * 필수 약관 동의 여부 검증
     */
    private void validateRequiredTermsAgreed(TermsDetail terms) {
        Boolean isRequired = getTermsIsRequired(terms.title(), terms.version());

        if (isRequired && !terms.agreed()) {
            throw new BadRequestException(terms.title() + " : 필수 약관에 대해 동의하지 않았습니다.");
        }
    }

    private Boolean getTermsIsRequired(String title, String version) {
        return termsRepository.findTermsIsRequired(title, version)
                .orElseThrow(() -> new NotFoundException("약관을 찾을 수 없습니다."));
    }
}
