package tosiltosil.backend.module.terms.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tosiltosil.backend.common.domain.exception.BadRequestException;
import tosiltosil.backend.common.domain.exception.NotFoundException;
import tosiltosil.backend.module.terms.domain.MemberTerms;
import tosiltosil.backend.module.terms.domain.Terms;
import tosiltosil.backend.module.terms.domain.request.TermsDetail;
import tosiltosil.backend.module.terms.infrastructure.MemberTermsJpaRepository;
import tosiltosil.backend.module.terms.infrastructure.TermsJpaRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TermsService {

    private final MemberTermsJpaRepository memberTermsJpaRepository;
    private final TermsJpaRepository termsJpaRepository;

    public void validateTerms(
            final List<TermsDetail> termsDetails
    ) {
        termsDetails.forEach(terms -> {
            if (terms.required() && !terms.agreed()) {
                throw new BadRequestException("필수 약관에 대해 동의하지 않았습니다.");
            }
        });
    }

    public Long getTermsId(final TermsDetail termsDetail) {
        Terms terms = termsJpaRepository.findByTitleAndVersion(termsDetail.title(), termsDetail.version())
                .orElseThrow(() -> new NotFoundException("약관을 찾을 수 없습니다."));
        return terms.getId();
    }

    public void saveTerms(
            final UUID memberId,
            final List<TermsDetail> termsDetails
    ) {
        termsDetails.forEach(terms -> {
            MemberTerms memberTerms = terms.toEntities(memberId, getTermsId(terms));
            memberTermsJpaRepository.save(memberTerms);
        });
    }
}
