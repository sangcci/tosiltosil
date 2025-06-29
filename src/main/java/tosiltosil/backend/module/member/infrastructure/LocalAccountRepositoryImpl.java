package tosiltosil.backend.module.member.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tosiltosil.backend.module.member.domain.LocalAccount;
import tosiltosil.backend.module.member.domain.LocalAccountRepository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LocalAccountRepositoryImpl implements LocalAccountRepository {

    private final localAccountJpaRepository localAccountJpaRepository;
    private final LocalAccountDslRepository localAccountDslRepository;

    @Override
    public String getPassword(UUID memberId) {
        return localAccountDslRepository.getPassword(memberId);
    }

    @Override
    public void save(LocalAccount LocalAccount) {
        localAccountJpaRepository.save(LocalAccount);
    }

}
