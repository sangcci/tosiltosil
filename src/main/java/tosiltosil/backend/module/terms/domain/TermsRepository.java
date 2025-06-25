package tosiltosil.backend.module.terms.domain;

import java.util.List;
import java.util.Optional;

public interface TermsRepository {
    Optional<Long> findVersionId(String title, String version);
    Optional<Boolean> findTermsIsRequired(String title, String version);
    List<String> findTitleList();
    Optional<String> findLastVersion(String title);
}
