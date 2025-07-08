package tosiltosil.backend.module.category.infrastructure;

import static tosiltosil.backend.module.category.domain.QCategory.category;
import static tosiltosil.backend.module.goal.domain.QGoal.goal;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tosiltosil.backend.module.category.domain.response.CategoryColorPerDayResponse;

@Repository
@RequiredArgsConstructor
public class CategoryDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<CategoryColorPerDayResponse> findColorsPerMonth(
            final UUID memberId,
            final YearMonth yearMonth
    ) {
        return queryFactory
                .select(Projections.constructor(CategoryColorPerDayResponse.class,
                        category.date,
                        category.color
                ))
                .from(category)
                .join(goal)
                .on(category.id.eq(goal.categoryId))
                .where(
                        goal.memberId.eq(memberId),
                        goal.date.between(yearMonth.atDay(1), yearMonth.atEndOfMonth())
                )
                .orderBy(category.date.asc())
                .fetch();
    }
}
