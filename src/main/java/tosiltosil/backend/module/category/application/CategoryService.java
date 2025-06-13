package tosiltosil.backend.module.category.application;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tosiltosil.backend.module.category.domain.Category;
import tosiltosil.backend.module.category.domain.CategoryRepository;
import tosiltosil.backend.module.category.domain.request.CategoryCreateRequest;
import tosiltosil.backend.module.category.domain.response.CategoryResponse;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse createCategory(
            final UUID memberId,
            final CategoryCreateRequest request
    ) {
        validateCreateCategory(memberId);

        // TODO: 순서 구현

        Category category = request.toEntity(memberId);
        Category savedCategory = categoryRepository.save(category);

        return CategoryResponse.of(savedCategory.getId());
    }

    private void validateCreateCategory(final UUID memberId) {
        Long numberOfCategories = categoryRepository.countByMemberId(memberId);
        if (numberOfCategories >= 10L) {
            throw new IllegalStateException("생성 제한을 넘어 카테고리를 생성할 수 없습니다.");
        }
    }
}
