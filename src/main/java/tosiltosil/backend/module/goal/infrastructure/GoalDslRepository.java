package tosiltosil.backend.module.goal.infrastructure;

import static tosiltosil.backend.module.category.domain.QCategory.category;
import static tosiltosil.backend.module.goal.domain.QGoal.goal;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tosiltosil.backend.module.goal.domain.response.DayGoalListResponse;

@Repository
@RequiredArgsConstructor
public class GoalDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<DayGoalListResponse> findDayGoals(
            final UUID memberId,
            final LocalDate date
    ) {
        return queryFactory
                .from(goal)
                .join(category)
                .on(goal.categoryId.eq(category.id))
                .where(
                        goal.memberId.eq(memberId),
                        goal.date.eq(date),
                        category.deleted.eq(false)
                )
                .orderBy(category.sequence.asc(), goal.sequence.asc())
                .transform(GroupBy.groupBy(category.id).list(
                        Projections.constructor(DayGoalListResponse.class,
                                category.id,
                                category.title,
                                category.color,
                                GroupBy.list(
                                        Projections.constructor(DayGoalListResponse.GoalListResponse.class,
                                                goal.id,
                                                category.id,
                                                goal.iconId,
                                                goal.title,
                                                goal.status.stringValue(),
                                                Expressions.stringTemplate("CAST({0} AS VARCHAR)", goal.totalTime),
                                                Expressions.stringTemplate("CAST({0} AS VARCHAR)", goal.duration)
                                        )
                                )
                        )
                ));
    }
}
