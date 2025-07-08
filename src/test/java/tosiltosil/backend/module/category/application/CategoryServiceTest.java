package tosiltosil.backend.module.category.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import tosiltosil.backend.module.category.domain.event.CategoryDeletedEvent;
import tosiltosil.backend.module.category.domain.request.CategoryCreateRequest;
import tosiltosil.backend.module.category.domain.response.CategoryListResponse;
import tosiltosil.backend.module.category.domain.response.CategoryResponse;
import tosiltosil.backend.module.duration.application.DurationService;
import tosiltosil.backend.module.goal.application.GoalService;
import tosiltosil.backend.module.goal.domain.Goal;
import tosiltosil.backend.module.goal.domain.GoalRepository;
import tosiltosil.backend.module.goal.domain.request.GoalCreateRequest;
import tosiltosil.backend.module.goal.domain.response.GoalIdsResponse;
import tosiltosil.backend.support.IntegrationTestSupport;

@SuppressWarnings("NonAsciiCharacters")
class CategoryServiceTest extends IntegrationTestSupport {

    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private GoalService goalService;
    
    @Autowired
    private GoalRepository goalRepository;
    
    @Autowired
    private DurationService durationService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    void 사용자_별_카테고리_이름_중복_가능() {
        // given
        UUID memberId = UUID.fromString("55797505-42ee-421c-a89e-5103c845e71b");
        
        CategoryCreateRequest request = new CategoryCreateRequest("자기개발", "#FF0000");
        CategoryCreateRequest requestHasSameTitle = new CategoryCreateRequest("자기개발", "#0000FF");
        
        // when
        CategoryResponse response = categoryService.createCategory(memberId, request);
        CategoryResponse responseHasSameTitle = categoryService.createCategory(memberId, requestHasSameTitle);
        
        // then
        assertThat(response.categoryId()).isNotEqualTo(responseHasSameTitle.categoryId());
    }

    @Test
    void 카테고리_삭제_시_카테고리에_속한_목표_전체_삭제() {

    }

    @Test
    void 카테고리_삭제_시_총_기록시간_롤백() {
        // given
        UUID memberId = UUID.fromString("55797505-42ee-421c-a89e-5103c845e71b");
        
        // 1. 카테고리 생성
        CategoryCreateRequest categoryRequest = new CategoryCreateRequest("자기개발", "#FF0000");
        CategoryResponse category = categoryService.createCategory(memberId, categoryRequest);
        
        // 2. 목표 생성
        GoalCreateRequest goalRequest = new GoalCreateRequest(
                "운동하기",
                1L, 
                category.categoryId(), 
                List.of("2025-07-08"),
                "PT1H"
        );
        GoalIdsResponse goalResponse = goalService.createGoal(memberId, goalRequest);
        
        // 3. 목표에 duration 설정 (30분)
        Goal goal = goalRepository.findById(goalResponse.goalIds().get(0)).get();
        Duration testDuration = Duration.ofMinutes(30);
        ReflectionTestUtils.setField(goal, "duration", testDuration);
        goalRepository.save(goal);

        // 4. 총 기록시간에 30분 추가
        durationService.updateTodayDuration(memberId, testDuration);
        
        // when
        categoryService.deleteCategory(memberId, category.categoryId());
        
        // then
        Duration afterDelete = durationService.getTodayDuration(memberId);
        assertThat(afterDelete).isEqualTo(Duration.ZERO);

        // verify
        assertThat(events.stream(CategoryDeletedEvent.class).count()).isEqualTo(1);
        assertSoftly(softly -> {
            events.stream(CategoryDeletedEvent.class).findFirst().ifPresent(event -> {
                softly.assertThat(event.memberId()).isEqualTo(memberId);
                softly.assertThat(event.deletedTotalDuration()).isEqualTo(testDuration);
            });
        });
    }

    @Test
    void 회원의_특정_날짜_카테고리_목록_조회() {
        // given
        UUID memberOwnerId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2025, 7, 8);

        // 카테고리 생성
        CategoryCreateRequest categoryRequest1 = new CategoryCreateRequest("운동", "#FF0000");
        CategoryCreateRequest categoryRequest2 = new CategoryCreateRequest("공부", "#00FF00");
        CategoryCreateRequest categoryRequest3 = new CategoryCreateRequest("업무", "#0000FF");

        categoryService.createCategory(memberId, categoryRequest1);
        categoryService.createCategory(memberId, categoryRequest2);
        categoryService.createCategory(memberId, categoryRequest3);

        // when
        List<CategoryListResponse> result = categoryService.getCategoriesByMemberId(memberOwnerId, memberId, date);

        // then
        assertThat(result).hasSize(3);
        assertSoftly(softly -> {
            softly.assertThat(result).extracting(CategoryListResponse::title)
                    .containsExactlyInAnyOrder("운동", "공부", "업무");
            softly.assertThat(result).extracting(CategoryListResponse::color)
                    .containsExactlyInAnyOrder("#FF0000", "#00FF00", "#0000FF");
        });
    }

    @Test
    void 회원의_특정_날짜에_카테고리가_없을_때_빈_목록_반환() {
        // given
        UUID memberOwnerId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2025, 7, 8);

        // when
        List<CategoryListResponse> result = categoryService.getCategoriesByMemberId(memberOwnerId, memberId, date);

        // then
        assertThat(result).isEmpty();
    }
}
