package tosiltosil.backend.module.category.domain.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tosiltosil.backend.module.category.domain.CategoryRepository;

@Component
@RequiredArgsConstructor
public class CategoryDomainService {

    private static final Long MAX_CATEGORIES_PER_MEMBER = 10L;

    private final CategoryRepository categoryRepository;

    public void validateCategoryCreation(final UUID memberId) {
        Long count = categoryRepository.countByMemberId(memberId);
        if (count >= MAX_CATEGORIES_PER_MEMBER) {
            throw new IllegalStateException("생성 제한을 넘어 카테고리를 생성할 수 없습니다.");
        }
    }
}
