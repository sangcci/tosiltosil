package tosiltosil.backend.module.terms.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tosiltosil.backend.module.terms.domain.TermsRepository;

import java.util.Optional;

import static tosiltosil.backend.module.terms.domain.QTerms.terms;
import static tosiltosil.backend.module.terms.domain.QTermsVersion.termsVersion;

@Repository
@RequiredArgsConstructor
public class TermsRepositoryImpl implements TermsRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Long> findVersionId(String title, String version) {
        return Optional.ofNullable(
                queryFactory
                        .select(termsVersion.id)
                        .from(termsVersion)
                        .join(terms).on(termsVersion.termsId.eq(terms.id))
                        .where(
                                terms.title.eq(title),
                                termsVersion.version.eq(version)
                        )
                        .fetchOne()
        );
    }

    @Override
    public Optional<Boolean> findTermsIsRequired(String title, String version) {
        return Optional.ofNullable(
                queryFactory
                        .select(terms.isRequired)
                        .from(terms)
                        .join(termsVersion).on(termsVersion.termsId.eq(terms.id))
                        .where(
                                terms.title.eq(title),
                                termsVersion.version.eq(version)
                        )
                        .fetchOne()
        );
    }

}
