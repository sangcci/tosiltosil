package tosiltosil.backend.module.member.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tosiltosil.backend.module.member.domain.LocalAccount;
import tosiltosil.backend.module.member.domain.LocalAccountRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LocalAccountRepositoryImpl implements LocalAccountRepository {

    private final LocalAccountJpaRepository localAccountJpaRepository;
    private final LocalAccountDslRepository localAccountDslRepository;

    @Override
    public Optional<String> findPasswordByMemberId(UUID memberId) {
        return localAccountDslRepository.findPasswordByMemberId(memberId);
    }

    @Override
    public void save(LocalAccount localAccount) {
        localAccountJpaRepository.save(localAccount);
    }

}
