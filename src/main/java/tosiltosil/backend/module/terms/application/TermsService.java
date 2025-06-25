package tosiltosil.backend.module.terms.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tosiltosil.backend.common.domain.exception.NotFoundException;
import tosiltosil.backend.module.terms.domain.MemberTerms;
import tosiltosil.backend.module.terms.domain.TermsRepository;
import tosiltosil.backend.module.terms.domain.request.TermsDetail;
import tosiltosil.backend.module.terms.domain.validator.TermsValidator;
import tosiltosil.backend.module.terms.infrastructure.MemberTermsJpaRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TermsService {

    private final MemberTermsJpaRepository memberTermsJpaRepository;
    private final TermsRepository termsRepository;
    private final TermsValidator termsValidator;

    public void validateTerms(final List<TermsDetail> termsDetails) {
        termsValidator.validateTerms(termsDetails);
    }

    private Long getTermsVersionId(
            final String title,
            final String version
    ) {
        return termsRepository.findVersionId(title, version)
                .orElseThrow(() -> new NotFoundException("약관을 찾을 수 없습니다."));
    }

    public void saveTerms(
            final UUID memberId,
            final List<TermsDetail> termsDetails
    ) {
        termsDetails.forEach(terms -> {
            MemberTerms memberTerms = terms.toEntities(
                    memberId,
                    getTermsVersionId(terms.title(), terms.version())
            );
            memberTermsJpaRepository.save(memberTerms);
        });
    }
}
