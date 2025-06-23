package tosiltosil.backend.module.terms.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tosiltosil.backend.module.terms.domain.TermsRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TermsRepositoryImpl implements TermsRepository {

    private final TermsDslRepository termsDslRepository;

    @Override
    public Optional<Long> findVersionId(String title, String version) {
        return termsDslRepository.findVersionId(title, version);
    }

    @Override
    public Optional<Boolean> findTermsIsRequired(String title, String version) {
        return termsDslRepository.findTermsIsRequired(title, version);
    }

    @Override
    public List<String> findTitleList() {
        return termsDslRepository.findTitleList();
    }

    @Override
    public Optional<String> findLastVersion(String title) {
        return termsDslRepository.findLastVersion(title);
    }
}
