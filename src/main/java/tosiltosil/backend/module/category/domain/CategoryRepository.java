package tosiltosil.backend.module.category.domain;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import tosiltosil.backend.module.category.domain.response.CategoryColorPerDayResponse;

public interface CategoryRepository {

    Optional<Category> findById(Long categoryId);

    List<Category> findCurrentCategories(UUID memberId);

    List<CategoryColorPerDayResponse> findColorsPerMonth(UUID memberId, YearMonth yearMonth);

    Long countCurrentCategory(UUID memberId);

    Optional<BigDecimal> findLastOrderIndex(UUID memberId);

    Category save(Category category);

    List<Category> saveAll(List<Category> categories);

    void delete(Category category);
}
