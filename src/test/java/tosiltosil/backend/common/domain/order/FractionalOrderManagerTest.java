package tosiltosil.backend.common.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
public class FractionalOrderManagerTest {

    private final FractionalOrderManager fractionalOrderManager;

    public FractionalOrderManagerTest() {
        this.fractionalOrderManager = new FractionalOrderManager();
    }

    @Test
    void 초기_인덱스_생성() {
        // when
        Double initialIndex = fractionalOrderManager.generateInitialOrderIndex();

        // then
        assertThat(initialIndex).isEqualTo(1000000.0);
    }

    @Test
    void 양쪽_인덱스가_모두_null인_경우_초기_인덱스_반환() {
        // when
        Double index = fractionalOrderManager.generateOrderIndexBetween(null, null);

        // then
        assertThat(index).isEqualTo(1000000.0);
    }

    @Test
    void 이전_인덱스가_null인_경우_다음_인덱스_앞에_위치하는_인덱스_생성() {
        // given
        Double nextIndex = 2000000.0;
        Double expectedIndex = nextIndex / 2;

        // when
        Double index = fractionalOrderManager.generateOrderIndexBetween(null, nextIndex);

        // then
        assertThat(index).isLessThan(nextIndex);
        assertThat(index).isEqualTo(expectedIndex);
    }

    @Test
    void 다음_인덱스가_null인_경우_이전_인덱스_뒤에_위치하는_인덱스_생성() {
        // given
        Double prevIndex = 1000000.0;
        Double expectedIndex = prevIndex + 1000000.0;

        // when
        Double index = fractionalOrderManager.generateOrderIndexBetween(prevIndex, null);

        // then
        assertThat(index).isGreaterThan(prevIndex);
        assertThat(index).isEqualTo(expectedIndex);
    }

    @Test
    void 두_인덱스_사이에_위치하는_인덱스_생성() {
        // given
        Double prevIndex = 1000000.0;
        Double nextIndex = 2000000.0;
        Double expectedIndex = (prevIndex + nextIndex) / 2;

        // when
        Double index = fractionalOrderManager.generateOrderIndexBetween(prevIndex, nextIndex);

        // then
        assertThat(index).isGreaterThan(prevIndex);
        assertThat(index).isLessThan(nextIndex);
        assertThat(index).isEqualTo(expectedIndex);
    }

    @Test
    void 이전_인덱스가_다음_인덱스보다_큰_경우_예외_발생() {
        // given
        Double prevIndex = 2000000.0;
        Double nextIndex = 1000000.0;

        // when & then
        assertThatThrownBy(() -> fractionalOrderManager.generateOrderIndexBetween(prevIndex, nextIndex))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Previous index must be less than next index");
    }

    @Test
    void 이전_인덱스와_다음_인덱스가_같은_경우_예외_발생() {
        // given
        Double prevIndex = 1000000.0;
        Double nextIndex = 1000000.0;

        // when & then
        assertThatThrownBy(() -> fractionalOrderManager.generateOrderIndexBetween(prevIndex, nextIndex))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Previous index must be less than next index");
    }

    @Test
    void 두_인덱스_차이가_너무_작은_경우_예외_발생() {
        // given
        Double prevIndex = 1000000.0;
        Double nextIndex = 1000000.000001; // MIN_INCREMENT와 같은 차이

        // when & then
        assertThatThrownBy(() -> fractionalOrderManager.generateOrderIndexBetween(prevIndex, nextIndex))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Indexes are too close to insert between");
    }

    @Test
    void 연속적인_인덱스_생성_테스트() {
        // given
        Double first = fractionalOrderManager.generateInitialOrderIndex();
        Double second = fractionalOrderManager.generateOrderIndexBetween(first, null);
        Double third = fractionalOrderManager.generateOrderIndexBetween(second, null);

        // when
        Double between1and2 = fractionalOrderManager.generateOrderIndexBetween(first, second);
        Double between2and3 = fractionalOrderManager.generateOrderIndexBetween(second, third);

        // then
        assertThat(first).isLessThan(between1and2);
        assertThat(between1and2).isLessThan(second);
        assertThat(second).isLessThan(between2and3);
        assertThat(between2and3).isLessThan(third);
    }

    @Test
    void 소수점_정밀도_테스트() {
        // given
        Double prev = 1000000.0;
        Double next = 1000000.000002; // MIN_INCREMENT * 2
        Double expectedIndex = (prev + next) / 2; // 1000000.000001

        // when
        Double index = fractionalOrderManager.generateOrderIndexBetween(prev, next);

        // then
        assertThat(index).isGreaterThan(prev);
        assertThat(index).isLessThan(next);
        assertThat(index).isEqualTo(expectedIndex);
    }

    @Test
    void 여러_엔티티_올바른_순서로_갱신() {
        // given
        TestOrderableEntity entity1 = new TestOrderableEntity(1500000.0);
        TestOrderableEntity entity2 = new TestOrderableEntity(1000000.000001);
        TestOrderableEntity entity3 = new TestOrderableEntity(1000000.000002);
        List<TestOrderableEntity> entities = List.of(entity1, entity2, entity3);

        // when
        List<TestOrderableEntity> result = fractionalOrderManager.renewOrderIndexes(entities);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getOrderIndex()).isEqualTo(1000000.0);  // 1000000 + (0 * 1000000)
        assertThat(result.get(1).getOrderIndex()).isEqualTo(2000000.0);  // 1000000 + (1 * 1000000)
        assertThat(result.get(2).getOrderIndex()).isEqualTo(3000000.0);  // 1000000 + (2 * 1000000)
    }

    @Test
    void renewOrderIndexes_원본_리스트_수정되지_않음() {
        // given
        TestOrderableEntity entity1 = new TestOrderableEntity(1500000.0);
        TestOrderableEntity entity2 = new TestOrderableEntity(1000000.000001);
        List<TestOrderableEntity> originalEntities = new ArrayList<>();
        originalEntities.add(entity1);
        originalEntities.add(entity2);
        
        Double originalIndex1 = entity1.getOrderIndex();
        Double originalIndex2 = entity2.getOrderIndex();

        // when
        List<TestOrderableEntity> result = fractionalOrderManager.renewOrderIndexes(originalEntities);

        // then
        assertThat(result).isSameAs(originalEntities); // 같은 리스트 객체 반환
        assertThat(entity1.getOrderIndex()).isNotEqualTo(originalIndex1); // 인덱스는 변경됨
        assertThat(entity2.getOrderIndex()).isNotEqualTo(originalIndex2);
    }

    private static class TestOrderableEntity implements Orderable {
        private Double orderIndex;

        public TestOrderableEntity(final Double orderIndex) {
            this.orderIndex = orderIndex;
        }

        @Override
        public Double getOrderIndex() {
            return orderIndex;
        }

        @Override
        public void updateOrderIndex(Double orderIndex) {
            this.orderIndex = orderIndex;
        }
    }
}