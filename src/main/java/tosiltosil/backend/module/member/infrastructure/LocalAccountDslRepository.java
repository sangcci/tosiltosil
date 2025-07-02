package tosiltosil.backend.module.member.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tosiltosil.backend.module.member.domain.LocalAccount;

import java.util.Optional;
import java.util.UUID;

import static tosiltosil.backend.module.member.domain.QLocalAccount.localAccount;

@Repository
@RequiredArgsConstructor
public class LocalAccountDslRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<String> findPasswordByMemberId(UUID memberId) {
        return Optional.ofNullable(queryFactory
                        .selectFrom(localAccount)
                        .where(localAccount.memberId.eq(memberId))
                        .fetchOne())
                        .map(LocalAccount::getPassword);
    }
}
