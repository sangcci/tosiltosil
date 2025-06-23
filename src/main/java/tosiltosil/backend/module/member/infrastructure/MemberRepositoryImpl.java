package tosiltosil.backend.module.member.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tosiltosil.backend.module.member.domain.MemberRepository;
import tosiltosil.backend.module.member.domain.value.LoginType;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberDslRepository memberDslRepository;

    @Override
    public boolean existsByCode(String code) {
        return memberDslRepository.existsByCode(code);
    }

    @Override
    public boolean existsByEmailAndLoginType(String email, LoginType loginType) {
        return memberDslRepository.existsByEmailAndLoginType(email, loginType);
    }
}
