package tosiltosil.backend.module.terms.domain.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tosiltosil.backend.common.domain.exception.BadRequestException;
import tosiltosil.backend.common.domain.exception.NotFoundException;
import tosiltosil.backend.module.terms.domain.TermsRepository;
import tosiltosil.backend.module.terms.domain.request.TermsDetail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TermsValidator {

    private final TermsRepository termsRepository;

    private static final String INVALID_TERMS_COUNT = "전달된 약관의 수가 올바르지 않습니다.";
    private static final String TERMS_TITLES_NOT_MATCHED = "약관 항목이 일치하지 않습니다.";
    private static final String TERMS_NOT_FOUND = "약관을 찾을 수 없습니다.";
    private static final String NOT_LATEST_VERSION = "최신 버전의 약관이 아닙니다.";
    private static final String REQUIRED_NOT_AGREED = "필수 약관을 동의하지 않았습니다.";

    /**
     * 전달된 모든 약관에 대한 검증
     */
    public void validateTerms(List<TermsDetail> termsDetail) {
        List<String> termsTitles = termsRepository.findTitleList();

        validateTermsCount(termsDetail, termsTitles.size());
        validateAllTermsIncluded(termsDetail, termsTitles);

        termsDetail.forEach(this::validateSingleTerms);
    }

    /**
     * 단일 약관에 대한 검증
     */
    private void validateSingleTerms(TermsDetail terms) {
        validateTermsExist(terms);
        validateRecentVersion(terms);
        validateRequiredTermsAgreed(terms);
    }

    /**
     * 전달된 약관의 수가 DB에 저장된 약관 개수가 동일한지 검증
     */
    void validateTermsCount(List<TermsDetail> termsDetails, int loadTermsCount) {
        if (termsDetails.size() != loadTermsCount)
            throw new BadRequestException(INVALID_TERMS_COUNT);
    }

    /**
     * 전달된 약관 목록이 DB에 있는 모든 약관을 포함하는지 검증
     */
    private void validateAllTermsIncluded(List<TermsDetail> termsDetail, List<String> expectedTitles) {
        Set<String> expected = new HashSet<>(expectedTitles);

        Set<String> receivedTitles = termsDetail.stream()
                .map(TermsDetail::title)
                .collect(Collectors.toSet());

        if (!expected.equals(receivedTitles)) {
            throw new BadRequestException(TERMS_TITLES_NOT_MATCHED);
        }
    }

    /**
     * 전달된 약관이 DB에 존재하는 값인지 검증
     */
    private void validateTermsExist(TermsDetail terms) {
        termsRepository.findVersionId(terms.title(), terms.version())
                .orElseThrow(() -> new NotFoundException(TERMS_NOT_FOUND));
    }

    /**
     * 전달된 필수 약관에 동의했는지 검증
     */
    private void validateRequiredTermsAgreed(TermsDetail terms) {
        Boolean isRequired = termsRepository.findTermsIsRequired(terms.title(), terms.version())
                .orElseThrow(() -> new NotFoundException(TERMS_NOT_FOUND));

        if (isRequired && !terms.agreed()) {
            throw new BadRequestException(REQUIRED_NOT_AGREED);
        }
    }

    /**
     * 전달된 약관이 최신 버전의 약관인지 검증
     */
    private void validateRecentVersion(TermsDetail terms) {
        String lastVersion = termsRepository.findLastVersion(terms.title())
                .orElseThrow(() -> new NotFoundException(TERMS_NOT_FOUND));

        if (!terms.version().equals(lastVersion)) {
            throw new BadRequestException(NOT_LATEST_VERSION);
        }
    }
}
