package tosiltosil.backend.module.category.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import tosiltosil.backend.common.domain.holder.TestTimeHolder;
import tosiltosil.backend.common.domain.holder.TimeHolder;
import tosiltosil.backend.module.category.application.CategoryServiceTest.TestTimeHolderConfig;
import tosiltosil.backend.module.category.domain.Category;
import tosiltosil.backend.module.category.domain.CategoryRepository;
import tosiltosil.backend.module.category.domain.event.CategoryDeletedEvent;
import tosiltosil.backend.module.category.domain.request.CategoryCreateRequest;
import tosiltosil.backend.module.category.domain.request.CategoryUpdateRequest;
import tosiltosil.backend.module.category.domain.response.CategoryResponse;
import tosiltosil.backend.module.category.domain.response.CurrentCategoryListResponse;
import tosiltosil.backend.module.category.infrastructure.CategoryJpaRepository;
import tosiltosil.backend.module.duration.application.DurationService;
import tosiltosil.backend.module.goal.application.GoalService;
import tosiltosil.backend.module.goal.domain.Goal;
import tosiltosil.backend.module.goal.domain.GoalRepository;
import tosiltosil.backend.module.goal.domain.request.GoalCreateRequest;
import tosiltosil.backend.module.goal.domain.response.GoalIdsResponse;
import tosiltosil.backend.module.goal.infrastructure.GoalJpaRepository;
import tosiltosil.backend.support.IntegrationTestSupport;

@Import(TestTimeHolderConfig.class)
@SuppressWarnings("NonAsciiCharacters")
class CategoryServiceTest extends IntegrationTestSupport {

    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private GoalService goalService;
    
    @Autowired
    private GoalRepository goalRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private DurationService durationService;

    @Autowired
    private TimeHolder timeHolder;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CategoryJpaRepository categoryJpaRepository;
    
    @Autowired
    private GoalJpaRepository goalJpaRepository;

    static class TestTimeHolderConfig {

        @Bean
        @Primary
        public TestTimeHolder testTimeHolder() {
            return new TestTimeHolder(LocalDate.of(2025, 7, 8));
        }
    }

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }
    
    @AfterEach
    void tearDown() {
        goalJpaRepository.deleteAllInBatch();
        categoryJpaRepository.deleteAllInBatch();
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    void 회원_현재_카테고리_목록_조회_시_지난_카테고리_미포함() {
        // given
        UUID memberId = UUID.fromString("55797505-42ee-421c-a89e-5103c845e71b");
        
        // 현재 카테고리 생성
        CategoryCreateRequest currentCategoryRequest = new CategoryCreateRequest("현재 카테고리", "#FF0000");
        CategoryResponse currentCategory = categoryService.createCategory(memberId, currentCategoryRequest);
        
        // 삭제될 카테고리 생성
        CategoryCreateRequest deletedCategoryRequest = new CategoryCreateRequest("삭제될 카테고리", "#00FF00");
        CategoryResponse deletedCategory = categoryService.createCategory(memberId, deletedCategoryRequest);
        
        // 카테고리 삭제 (지난 카테고리로 만들기)
        categoryService.deleteCategory(memberId, deletedCategory.categoryId());
        
        // when
        List<CurrentCategoryListResponse> currentCategories = categoryService.getCategoriesByMemberId(memberId);
        
        // then
        assertSoftly(softly -> {
            // 현재 카테고리만 조회되어야 함
            softly.assertThat(currentCategories).hasSize(1);
            softly.assertThat(currentCategories.get(0).categoryId()).isEqualTo(currentCategory.categoryId());
            softly.assertThat(currentCategories.get(0).title()).isEqualTo("현재 카테고리");
            softly.assertThat(currentCategories.get(0).color()).isEqualTo("#FF0000");
            
            // 삭제된 카테고리는 포함되지 않아야 함
            List<Long> categoryIds = currentCategories.stream()
                    .map(CurrentCategoryListResponse::categoryId)
                    .toList();
            softly.assertThat(categoryIds).doesNotContain(deletedCategory.categoryId());
        });
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
    void 카테고리_수정_시_목표에_카테고리_업데이트_반영() {
        // given
        UUID memberId = UUID.fromString("55797505-42ee-421c-a89e-5103c845e71b");
        LocalDate currentDate = timeHolder.getCurrentDate();

        // 1. 카테고리 직접 DB 삽입
        Category originalCategory = Category.of(memberId, "자기개발", "#FF0000");
        Category savedOriginalCategory = categoryRepository.save(originalCategory);
        
        // 2. 어제, 오늘, 내일 목표 직접 DB 삽입
        LocalDate yesterday = currentDate.minusDays(1);
        LocalDate today = currentDate;
        LocalDate tomorrow = currentDate.plusDays(1);
        
        Goal yesterdayGoal = Goal.of(memberId, savedOriginalCategory.getId(), "어제 목표", Duration.ofHours(1), 0, 1L, yesterday);
        Goal todayGoal = Goal.of(memberId, savedOriginalCategory.getId(), "오늘 목표", Duration.ofHours(1), 0, 1L, today);
        Goal tomorrowGoal = Goal.of(memberId, savedOriginalCategory.getId(), "내일 목표", Duration.ofHours(1), 0, 1L, tomorrow);
        
        Goal savedYesterdayGoal = goalRepository.save(yesterdayGoal);
        Goal savedTodayGoal = goalRepository.save(todayGoal);
        Goal savedTomorrowGoal = goalRepository.save(tomorrowGoal);
        
        Long yesterdayGoalId = savedYesterdayGoal.getId();
        Long todayGoalId = savedTodayGoal.getId();
        Long tomorrowGoalId = savedTomorrowGoal.getId();
        
        // when
        CategoryUpdateRequest updateRequest = new CategoryUpdateRequest("수정된 카테고리", "#00FF00");
        CategoryResponse updatedCategory = categoryService.updateCategory(memberId, savedOriginalCategory.getId(), updateRequest);
        
        // then
        // 1. 새로운 카테고리가 생성되어야 함
        assertThat(updatedCategory.categoryId()).isNotEqualTo(savedOriginalCategory.getId());
        
        // 2. 목표들의 카테고리 ID 검증
        Goal updatedYesterdayGoal = goalRepository.findById(yesterdayGoalId).get();
        Goal updatedTodayGoal = goalRepository.findById(todayGoalId).get();
        Goal updatedTomorrowGoal = goalRepository.findById(tomorrowGoalId).get();
        
        assertSoftly(softly -> {
            // 어제 목표는 원래 카테고리 ID 유지 (과거 데이터 보존)
            softly.assertThat(updatedYesterdayGoal.getCategoryId()).isEqualTo(savedOriginalCategory.getId());
            // 오늘 목표는 새로운 카테고리 ID로 업데이트
            softly.assertThat(updatedTodayGoal.getCategoryId()).isEqualTo(updatedCategory.categoryId());
            // 내일 목표는 새로운 카테고리 ID로 업데이트
            softly.assertThat(updatedTomorrowGoal.getCategoryId()).isEqualTo(updatedCategory.categoryId());
        });
    }

    @Test
    @Disabled
    void 카테고리_삭제_시_카테고리에_속한_목표_전체_삭제() {
        // given
        // when
        // then
        // TODO: 테스트 구현 예정
    }

    @Test
    void 카테고리_삭제_시_총_기록시간_롤백() {
        // given
        UUID memberId = UUID.fromString("55797505-42ee-421c-a89e-5103c845e71b");
        
        // 1. 카테고리 생성
        CategoryCreateRequest categoryRequest = new CategoryCreateRequest("자기개발", "#FF0000");
        CategoryResponse category = categoryService.createCategory(memberId, categoryRequest);
        
        // 2. 목표 생성
        GoalCreateRequest goalRequest = new GoalCreateRequest("운동하기", 1L, category.categoryId(), List.of("2025-07-08"), "PT1H");
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
}
