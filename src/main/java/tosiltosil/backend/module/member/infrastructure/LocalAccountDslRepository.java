package tosiltosil.backend.module.member.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.UUID;

import static tosiltosil.backend.module.member.domain.QLocalAccount.localAccount;

@Repository
@RequiredArgsConstructor
public class LocalAccountDslRepository {

    private final JPAQueryFactory queryFactory;

    public String getPassword(UUID memberId) {
        return Objects.requireNonNull(queryFactory
                        .selectFrom(localAccount)
                        .where(localAccount.memberId.eq(memberId))
                        .fetchOne())
                        .getPassword();
    }
}
