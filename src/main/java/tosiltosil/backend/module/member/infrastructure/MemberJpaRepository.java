package tosiltosil.backend.module.member.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import tosiltosil.backend.module.member.domain.Member;

import java.util.Optional;
import java.util.UUID;

public interface MemberJpaRepository extends JpaRepository<Member, UUID> {

    Optional<Member> findByEmail(String email);
}
