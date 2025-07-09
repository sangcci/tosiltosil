package tosiltosil.backend.module.category.domain;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import tosiltosil.backend.module.category.domain.response.CategoryColorPerDayResponse;

public interface CategoryRepository {

    Optional<Category> findById(Long categoryId);

    //List<Category> findCategoriesByMemberIdAndDate(UUID memberId, LocalDate date);

    List<CategoryColorPerDayResponse> findColorsPerMonth(UUID memberId, YearMonth yearMonth);

    Long countByMemberId(UUID memberId);

    Category save(Category category);

    void delete(Category category);
}
