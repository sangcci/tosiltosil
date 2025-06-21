package tosiltosil.backend.module.terms.domain.validator;

import tosiltosil.backend.module.terms.domain.request.TermsDetail;

import java.util.List;

public interface TermsValidator {
    void validateTerms(List<TermsDetail> termsDetails);
}
