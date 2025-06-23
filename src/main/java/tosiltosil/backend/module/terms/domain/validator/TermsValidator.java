package tosiltosil.backend.module.terms.domain.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tosiltosil.backend.common.domain.exception.BadRequestException;
import tosiltosil.backend.common.domain.exception.NotFoundException;
import tosiltosil.backend.module.terms.domain.TermsRepository;
import tosiltosil.backend.module.terms.domain.request.TermsDetail;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TermsValidator {

    private final TermsRepository termsRepository;

    private static final String TERMS_NOT_FOUND = "약관을 찾을 수 없습니다.";
    private static final String NOT_LATEST_VERSION = "최신 버전의 약관이 아닙니다.";
    private static final String REQUIRED_NOT_AGREED = "필수 약관을 동의하지 않았습니다.";
    private static final String DUPLICATE_TERMS = "중복된 약관 동의가 존재합니다.";

    private List<String> loadTermsTitle() {
        return termsRepository.findTitleList();
    }

    /**
     * 전체 약관 검증
     */
    public void validateTerms(List<TermsDetail> termsDetail) {
        validateDuplicateTerms(termsDetail);
        termsDetail.forEach(this::validateSingleTerms);
    }

    /**
     * 단일 약관 검증
     */
    private void validateSingleTerms(TermsDetail terms) {
        validateTermsExist(terms);
        validateRecentVersion(terms);
        validateRequiredTermsAgreed(terms);
    }

    /**
     * 중복된 약관 검증
     */
    private void validateDuplicateTerms(List<TermsDetail> termsDetails) {
        List<String> termsTitle = loadTermsTitle();

        long cnt = termsDetails.stream()
                .map(TermsDetail::title)
                .distinct()
                .count();

        if (termsDetails.size() == termsTitle.size() &&
                cnt != termsTitle.size())
            throw new BadRequestException(DUPLICATE_TERMS);
    }

    /**
     * 약관 존재 검증
     */
    private void validateTermsExist(TermsDetail terms) {
        termsRepository.findVersionId(terms.title(), terms.version())
                .orElseThrow(() -> new NotFoundException(TERMS_NOT_FOUND));
    }

    /**
     * 필수 약관 동의 여부 검증
     */
    private void validateRequiredTermsAgreed(TermsDetail terms) {
        Boolean isRequired = termsRepository.findTermsIsRequired(terms.title(), terms.version())
                .orElseThrow(() -> new NotFoundException(TERMS_NOT_FOUND));

        if (isRequired && !terms.agreed()) {
            throw new BadRequestException(REQUIRED_NOT_AGREED);
        }
    }

    /**
     * 최신 버전의 약관인지 검증
     */
    private void validateRecentVersion(TermsDetail terms) {
        String lastVersion = termsRepository.findLastVersion(terms.title())
                .orElseThrow(() -> new NotFoundException(TERMS_NOT_FOUND));

        if (!terms.version().equals(lastVersion)) {
            throw new BadRequestException(NOT_LATEST_VERSION);
        }
    }
}
