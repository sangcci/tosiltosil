package tosiltosil.backend.module.category.infrastructure;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static tosiltosil.backend.module.category.domain.QCategory.category;
import static tosiltosil.backend.module.goal.domain.QGoal.goal;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        // 1. 날짜 별 카테고리 색깔 모두 조회
        List<Tuple> fetch = queryFactory
                .selectDistinct(goal.date, category.color)
                .from(goal)
                .join(category)
                .on(goal.categoryId.eq(category.id))
                .where(
                        goal.memberId.eq(memberId),
                        goal.date.between(yearMonth.atDay(1), yearMonth.atEndOfMonth())
                        // TODO: 순서 조건 추가 및 목표 완료 상태 조건 추가
                )
                .orderBy(goal.date.asc(), category.color.asc())
                .fetch();

        // 2. 날짜 별 색깔 grouping
        Map<LocalDate, List<String>> grouping = fetch
                .stream()
                .collect(groupingBy(
                        tuple -> Objects.requireNonNull(tuple.get(goal.date)),
                        mapping(
                                tuple -> Objects.requireNonNull(tuple.get(category.color)),
                                toList()
                        )
                ));

        // 3. 색깔 2개 제한 stream 적용
        return grouping.entrySet().stream()
                .map(entry -> new CategoryColorPerDayResponse(
                        entry.getKey(),
                        entry.getValue().stream().limit(2).collect(toList())
                ))
                .collect(toList());
    }
}
