package tosiltosil.backend.module.goal.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
import tosiltosil.backend.common.domain.holder.TestTimeHolder;
import tosiltosil.backend.module.category.application.CategoryService;
import tosiltosil.backend.module.category.domain.request.CategoryCreateRequest;
import tosiltosil.backend.module.category.domain.response.CategoryResponse;
import tosiltosil.backend.module.category.infrastructure.CategoryJpaRepository;
import tosiltosil.backend.module.goal.application.GoalServiceTest.TestTimeHolderConfig;
import tosiltosil.backend.module.goal.domain.Goal;
import tosiltosil.backend.module.goal.domain.request.GoalCreateRequest;
import tosiltosil.backend.module.goal.domain.response.GoalIdsResponse;
import tosiltosil.backend.module.goal.infrastructure.GoalJpaRepository;
import tosiltosil.backend.support.IntegrationTestSupport;

@Import(TestTimeHolderConfig.class)
@SuppressWarnings("NonAsciiCharacters")
class GoalServiceTest extends IntegrationTestSupport {

    @Autowired
    private GoalService goalService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private GoalJpaRepository goalJpaRepository;

    @Autowired
    private CategoryJpaRepository categoryJpaRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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
    @Disabled
    void 친구의_특정_날짜_목표_목록_조회() {
        // given
        // when
        // then
        // TODO: 테스트 구현 예정
    }

    @Test
    void 기존_목표_있는_상태에서_새_목표_생성시_마지막_순서_다음으로_생성() {
        // given
        UUID memberId = UUID.fromString("55797505-42ee-421c-a89e-5103c845e71b");
        CategoryResponse category = categoryService.createCategory(memberId, new CategoryCreateRequest("카테고리", "RED"));
        
        // 기존 목표 생성
        GoalCreateRequest existingRequest = new GoalCreateRequest("기존 목표", 1L, category.categoryId(), List.of("2025-07-08"), "PT1H");
        GoalIdsResponse existingResponse = goalService.createGoal(memberId, existingRequest);
        
        // when - 새 목표 생성
        GoalCreateRequest newRequest = new GoalCreateRequest("새 목표", 2L, category.categoryId(), List.of("2025-07-08"), "PT2H");
        GoalIdsResponse newResponse = goalService.createGoal(memberId, newRequest);
        
        // then
        Goal existingGoal = goalJpaRepository.findById(existingResponse.goalIds().get(0)).get();
        Goal newGoal = goalJpaRepository.findById(newResponse.goalIds().get(0)).get();

        // 새 목표의 순서 인덱스가 기존 목표보다 커야 함
        assertThat(newGoal.getOrderIndex()).isGreaterThan(existingGoal.getOrderIndex());
    }
}
