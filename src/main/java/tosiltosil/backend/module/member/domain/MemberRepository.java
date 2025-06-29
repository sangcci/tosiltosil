package tosiltosil.backend.module.member.domain;

import tosiltosil.backend.module.member.domain.value.LoginType;

import java.util.Optional;

public interface MemberRepository {
    boolean existsByCode(String code);
    boolean existsByEmailAndLoginType(String email, LoginType loginType);
    Optional<Member> findByEmail(String email);
    void save(Member member);
}
