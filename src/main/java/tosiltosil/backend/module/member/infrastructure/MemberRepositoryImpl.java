package tosiltosil.backend.module.member.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tosiltosil.backend.module.member.domain.Member;
import tosiltosil.backend.module.member.domain.MemberRepository;
import tosiltosil.backend.module.member.domain.value.LoginType;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;
    private final MemberDslRepository memberDslRepository;

    @Override
    public boolean existsByCode(String code) {
        return memberDslRepository.existsByCode(code);
    }

    @Override
    public boolean existsByEmailAndLoginType(String email, LoginType loginType) {
        return memberDslRepository.existsByEmailAndLoginType(email, loginType);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return memberDslRepository.findByEmail(email);
    }

    @Override
    public void save(Member member) {
        memberJpaRepository.save(member);
    }
}
