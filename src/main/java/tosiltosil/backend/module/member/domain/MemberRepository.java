package tosiltosil.backend.module.member.domain;

import tosiltosil.backend.module.member.domain.value.LoginType;

public interface MemberRepository {
    boolean existsByCode(String code);
    boolean existsByEmailAndLoginType(String email, LoginType loginType);
}
