package tosiltosil.backend.module.category.infrastructure;

import static com.querydsl.core.types.Projections.list;
import static tosiltosil.backend.module.category.domain.QCategory.category;
import static tosiltosil.backend.module.goal.domain.QGoal.goal;

import com.querydsl.core.group.GroupBy;
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
                .from(goal)
                .join(category)
                .on(goal.categoryId.eq(category.id))
                .where(
                        goal.memberId.eq(memberId),
                        goal.date.between(yearMonth.atDay(1), yearMonth.atEndOfMonth())
                )
                .orderBy(goal.date.asc(), category.color.asc()) // 순서
                .transform(GroupBy.groupBy(goal.date).list(
                        Projections.constructor(CategoryColorPerDayResponse.class,
                                goal.date,
                                list(category.color)
                        )
                ));
    }
}
