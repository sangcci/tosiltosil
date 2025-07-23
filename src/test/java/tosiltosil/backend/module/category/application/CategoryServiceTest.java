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
import tosiltosil.backend.module.category.application.CategoryServiceTest.TestTimeHolderConfig;
import tosiltosil.backend.module.category.domain.Category;
import tosiltosil.backend.module.category.domain.event.CategoryDeletedEvent;
import tosiltosil.backend.module.category.domain.request.CategoryCreateRequest;
import tosiltosil.backend.module.category.domain.value.CategoryColor;
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
    private DurationService durationService;

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
        CategoryCreateRequest currentCategoryRequest = new CategoryCreateRequest("현재 카테고리", "RED");
        CategoryResponse currentCategory = categoryService.createCategory(memberId, currentCategoryRequest);
        
        // 삭제될 카테고리 생성
        CategoryCreateRequest deletedCategoryRequest = new CategoryCreateRequest("삭제될 카테고리", "ORANGE");
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
            softly.assertThat(currentCategories.get(0).color()).isEqualTo(CategoryColor.RED);
            
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
        
        CategoryCreateRequest request = new CategoryCreateRequest("자기개발", "RED");
        CategoryCreateRequest requestHasSameTitle = new CategoryCreateRequest("자기개발", "BLUE");
        
        // when
        CategoryResponse response = categoryService.createCategory(memberId, request);
        CategoryResponse responseHasSameTitle = categoryService.createCategory(memberId, requestHasSameTitle);
        
        // then
        assertThat(response.categoryId()).isNotEqualTo(responseHasSameTitle.categoryId());
    }

    @Test
    void 카테고리_생성시_올바른_순서_인덱스_생성() {
        // given
        UUID memberId = UUID.fromString("55797505-42ee-421c-a89e-5103c845e71b");
        
        // when
        CategoryResponse first = categoryService.createCategory(memberId, new CategoryCreateRequest("첫번째", "RED"));
        CategoryResponse second = categoryService.createCategory(memberId, new CategoryCreateRequest("두번째", "ORANGE"));
        CategoryResponse third = categoryService.createCategory(memberId, new CategoryCreateRequest("세번째", "BLUE"));
        
        // then
        List<CurrentCategoryListResponse> categories = categoryService.getCategoriesByMemberId(memberId);
        
        assertSoftly(softly -> {
            softly.assertThat(categories).hasSize(3);
            // 순서대로 정렬되어야 함
            softly.assertThat(categories.get(0).categoryId()).isEqualTo(first.categoryId());
            softly.assertThat(categories.get(1).categoryId()).isEqualTo(second.categoryId());
            softly.assertThat(categories.get(2).categoryId()).isEqualTo(third.categoryId());
        });
    }

    @Test
    void 여러_카테고리_생성시_순서_인덱스_순차적_증가() {
        // given
        UUID memberId = UUID.fromString("55797505-42ee-421c-a89e-5103c845e71b");
        
        // when
        CategoryResponse first = categoryService.createCategory(memberId, new CategoryCreateRequest("카테고리1", "RED"));
        CategoryResponse second = categoryService.createCategory(memberId, new CategoryCreateRequest("카테고리2", "ORANGE"));
        CategoryResponse third = categoryService.createCategory(memberId, new CategoryCreateRequest("카테고리3", "BLUE"));
        
        // then
        Category firstCategory = categoryJpaRepository.findById(first.categoryId()).get();
        Category secondCategory = categoryJpaRepository.findById(second.categoryId()).get();
        Category thirdCategory = categoryJpaRepository.findById(third.categoryId()).get();
        
        assertSoftly(softly -> {
            // 순서 인덱스가 순차적으로 증가해야 함
            softly.assertThat(firstCategory.getOrderIndex()).isLessThan(secondCategory.getOrderIndex());
            softly.assertThat(secondCategory.getOrderIndex()).isLessThan(thirdCategory.getOrderIndex());
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
        CategoryCreateRequest categoryRequest = new CategoryCreateRequest("자기개발", "RED");
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
