package tosiltosil.backend.module.member.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import tosiltosil.backend.module.member.domain.Member;
import tosiltosil.backend.module.member.domain.value.LoginType;

import java.util.UUID;

public interface MemberJpaRepository extends JpaRepository<Member, UUID> {

    boolean existsByCode(String code);

    boolean findByEmailAndLoginType(String email, LoginType loginType);
}
