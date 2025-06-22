package tosiltosil.backend.module.member.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tosiltosil.backend.module.member.domain.MemberRepository;
import tosiltosil.backend.module.member.domain.value.LoginType;

import static tosiltosil.backend.module.member.domain.QMember.member;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsByCode(String code) {
        return queryFactory
                .selectOne()
                .from(member)
                .where(
                        member.code.eq(code)
                )
                .fetchFirst() != null;
    }

    @Override
    public boolean existsByEmailAndLoginType(String email, LoginType loginType) {
        return queryFactory
                .selectOne()
                .from(member)
                .where(
                        member.email.eq(email),
                        member.loginType.eq(loginType)
                )
                .fetchFirst() != null;
    }
}
