package tosiltosil.backend.module.member.domain;

import java.util.UUID;

public interface LocalAccountRepository{
    String getPassword(UUID memberId);
    void save(LocalAccount localAccount);
}
