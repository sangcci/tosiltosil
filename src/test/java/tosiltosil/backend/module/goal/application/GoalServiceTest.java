package tosiltosil.backend.module.goal.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tosiltosil.backend.module.category.application.CategoryService;
import tosiltosil.backend.module.category.domain.request.CategoryCreateRequest;
import tosiltosil.backend.module.category.domain.response.CategoryResponse;
import tosiltosil.backend.module.goal.domain.request.GoalCreateRequest;
import tosiltosil.backend.module.goal.domain.response.GoalListResponse;
import tosiltosil.backend.support.IntegrationTestSupport;

@SuppressWarnings("NonAsciiCharacters")
class GoalServiceTest extends IntegrationTestSupport {

    @Autowired
    private GoalService goalService;

    @Autowired
    private CategoryService categoryService;

    @Test
    void 회원의_특정_날짜_목표_목록_조회() {
        // given
        UUID memberOwnerId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2025, 7, 8);

        // 카테고리 생성
        CategoryCreateRequest categoryRequest = new CategoryCreateRequest("자기개발", "#FF0000");
        CategoryResponse category = categoryService.createCategory(memberId, categoryRequest);

        // 목표 생성
        GoalCreateRequest goalRequest1 = new GoalCreateRequest("운동하기", 1L, category.categoryId(), List.of("2025-07-08"), "PT2H");
        GoalCreateRequest goalRequest2 = new GoalCreateRequest("독서하기", 2L, category.categoryId(), List.of("2025-07-08"), "PT1H");
        GoalCreateRequest goalRequest3 = new GoalCreateRequest("코딩하기", 3L, category.categoryId(), List.of("2025-07-08"), "PT3H");

        goalService.createGoal(memberId, goalRequest1);
        goalService.createGoal(memberId, goalRequest2);
        goalService.createGoal(memberId, goalRequest3);

        // when
        List<GoalListResponse> result = goalService.getGoalsByMemberCode(memberOwnerId, memberId, date);

        // then
        assertThat(result).hasSize(3);
        assertSoftly(softly -> {
            softly.assertThat(result).extracting(GoalListResponse::title)
                    .containsExactlyInAnyOrder("운동하기", "독서하기", "코딩하기");
            softly.assertThat(result).extracting(GoalListResponse::status)
                    .containsOnly("BEFORE_STARTING");
            softly.assertThat(result).extracting(GoalListResponse::categoryId)
                    .containsOnly(category.categoryId());
        });
    }

    @Test
    void 회원의_특정_날짜에_목표가_없을_때_빈_목록_반환() {
        // given
        UUID memberOwnerId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2025, 7, 8);

        // when
        List<GoalListResponse> result = goalService.getGoalsByMemberCode(memberOwnerId, memberId, date);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void 회원의_친구_목표_목록_조회() {
        // given
        UUID memberOwnerId = UUID.randomUUID();
        UUID memberFriendId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2025, 7, 8);

        // 카테고리 생성
        CategoryCreateRequest categoryRequest = new CategoryCreateRequest("업무", "#0000FF");
        CategoryResponse category = categoryService.createCategory(memberFriendId, categoryRequest);

        // 다른 회원의 목표 생성
        GoalCreateRequest goalRequest = new GoalCreateRequest("다른 회원의 목표", 1L, category.categoryId(), List.of("2025-07-08"), "PT1H");
        goalService.createGoal(memberFriendId, goalRequest);

        // when
        List<GoalListResponse> result = goalService.getGoalsByMemberCode(memberOwnerId, memberFriendId, date);

        // then
        assertThat(result).hasSize(1);
        assertSoftly(softly -> {
            GoalListResponse response = result.get(0);
            softly.assertThat(response.title()).isEqualTo("다른 회원의 목표");
            softly.assertThat(response.status()).isEqualTo("BEFORE_STARTING");
        });
    }
}