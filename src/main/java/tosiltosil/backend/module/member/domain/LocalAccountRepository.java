package tosiltosil.backend.module.member.domain;

import java.util.Optional;
import java.util.UUID;

public interface LocalAccountRepository{
    Optional<String> findPasswordByMemberId(UUID memberId);
    void save(LocalAccount localAccount);
}
