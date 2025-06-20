package tosiltosil.backend.module.terms.domain;

import java.util.Optional;

public interface TermsRepository {
    Optional<Long> findVersionId(String title, String version);
    Optional<Boolean> findTermsIsRequired(String title, String version);
}
