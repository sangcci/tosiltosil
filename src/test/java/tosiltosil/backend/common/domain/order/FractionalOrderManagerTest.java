package tosiltosil.backend.common.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tosiltosil.backend.common.domain.exception.BadRequestException;

@SuppressWarnings("NonAsciiCharacters")
public class FractionalOrderManagerTest {

    private final FractionalOrderManager fractionalOrderManager;

    public FractionalOrderManagerTest() {
        this.fractionalOrderManager = new FractionalOrderManager();
    }

    @Test
    void 초기_인덱스_생성() {
        // when
        BigDecimal initialIndex = fractionalOrderManager.generateInitialOrderIndex();

        // then
        assertThat(initialIndex).isEqualTo(BigDecimal.valueOf(1024));
    }

    @Test
    void 양쪽_인덱스가_모두_null인_경우_초기_인덱스_반환() {
        // when
        BigDecimal index = fractionalOrderManager.generateOrderIndexBetween(null, null);

        // then
        assertThat(index).isEqualTo(BigDecimal.valueOf(1024));
    }

    @Test
    void 이전_인덱스가_null인_경우_다음_인덱스_앞에_위치하는_인덱스_생성() {
        // given
        BigDecimal nextIndex = BigDecimal.valueOf(2048);
        BigDecimal expectedIndex = nextIndex.divide(BigDecimal.valueOf(2));

        // when
        BigDecimal index = fractionalOrderManager.generateOrderIndexBetween(null, nextIndex);

        // then
        assertThat(index).isLessThan(nextIndex);
        assertThat(index).isEqualTo(expectedIndex);
    }

    @Test
    void 다음_인덱스가_null인_경우_이전_인덱스_뒤에_위치하는_인덱스_생성() {
        // given
        BigDecimal prevIndex = BigDecimal.valueOf(1024);
        BigDecimal expectedIndex = prevIndex.add(BigDecimal.valueOf(1024));

        // when
        BigDecimal index = fractionalOrderManager.generateOrderIndexBetween(prevIndex, null);

        // then
        assertThat(index).isGreaterThan(prevIndex);
        assertThat(index).isEqualTo(expectedIndex);
    }

    @Test
    void 두_인덱스_사이에_위치하는_인덱스_생성() {
        // given
        BigDecimal prevIndex = BigDecimal.valueOf(1024);
        BigDecimal nextIndex = BigDecimal.valueOf(2048);
        BigDecimal expectedIndex = prevIndex.add(nextIndex).divide(BigDecimal.valueOf(2), 3, RoundingMode.HALF_UP);

        // when
        BigDecimal index = fractionalOrderManager.generateOrderIndexBetween(prevIndex, nextIndex);

        // then
        assertThat(index).isGreaterThan(prevIndex);
        assertThat(index).isLessThan(nextIndex);
        assertThat(index).isEqualTo(expectedIndex);
    }

    @Test
    void 두_인덱스_차이가_너무_작은_경우_예외_발생() {
        // given
        BigDecimal prevIndex = BigDecimal.valueOf(1024);
        BigDecimal nextIndex = BigDecimal.valueOf(1024.001); // MIN_INCREMENT와 같은 차이

        // when & then
        assertThatThrownBy(() -> fractionalOrderManager.generateOrderIndexBetween(prevIndex, nextIndex))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("인덱스 변경 한계에 도달하였습니다.");
    }

    @Test
    void 연속적인_인덱스_생성_테스트() {
        // given
        BigDecimal first = fractionalOrderManager.generateInitialOrderIndex();
        BigDecimal second = fractionalOrderManager.generateOrderIndexBetween(first, null);
        BigDecimal third = fractionalOrderManager.generateOrderIndexBetween(second, null);

        // when
        BigDecimal between1and2 = fractionalOrderManager.generateOrderIndexBetween(first, second);
        BigDecimal between2and3 = fractionalOrderManager.generateOrderIndexBetween(second, third);

        // then
        assertThat(first).isLessThan(between1and2);
        assertThat(between1and2).isLessThan(second);
        assertThat(second).isLessThan(between2and3);
        assertThat(between2and3).isLessThan(third);
    }

    @Test
    void 소수점_정밀도_테스트() {
        // given
        BigDecimal prev = BigDecimal.valueOf(1024);
        BigDecimal next = BigDecimal.valueOf(1024.002); // MIN_INCREMENT * 2
        BigDecimal expectedIndex = prev.add(next).divide(BigDecimal.valueOf(2), 3, RoundingMode.HALF_UP); // 1024.001

        // when
        BigDecimal index = fractionalOrderManager.generateOrderIndexBetween(prev, next);

        // then
        assertThat(index).isGreaterThan(prev);
        assertThat(index).isLessThan(next);
        assertThat(index).isEqualTo(expectedIndex);
    }

    @Test
    void 여러_엔티티_올바른_순서로_갱신() {
        // given
        TestOrderableEntity entity1 = new TestOrderableEntity(BigDecimal.valueOf(1536));
        TestOrderableEntity entity2 = new TestOrderableEntity(BigDecimal.valueOf(1024.001));
        TestOrderableEntity entity3 = new TestOrderableEntity(BigDecimal.valueOf(1024.002));
        List<TestOrderableEntity> entities = List.of(entity1, entity2, entity3);

        // when
        List<TestOrderableEntity> result = fractionalOrderManager.renewOrderIndexes(entities);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getOrderIndex()).isEqualTo(BigDecimal.valueOf(1024));  // 1024 + (0 * 1024)
        assertThat(result.get(1).getOrderIndex()).isEqualTo(BigDecimal.valueOf(2048));  // 1024 + (1 * 1024)
        assertThat(result.get(2).getOrderIndex()).isEqualTo(BigDecimal.valueOf(3072));  // 1024 + (2 * 1024)
    }

    @Test
    void renewOrderIndexes_원본_리스트_수정되지_않음() {
        // given
        TestOrderableEntity entity1 = new TestOrderableEntity(BigDecimal.valueOf(1536));
        TestOrderableEntity entity2 = new TestOrderableEntity(BigDecimal.valueOf(1024.001));
        List<TestOrderableEntity> originalEntities = new ArrayList<>();
        originalEntities.add(entity1);
        originalEntities.add(entity2);
        
        BigDecimal originalIndex1 = entity1.getOrderIndex();
        BigDecimal originalIndex2 = entity2.getOrderIndex();

        // when
        List<TestOrderableEntity> result = fractionalOrderManager.renewOrderIndexes(originalEntities);

        // then
        assertThat(result).isSameAs(originalEntities); // 같은 리스트 객체 반환
        assertThat(entity1.getOrderIndex()).isNotEqualTo(originalIndex1); // 인덱스는 변경됨
        assertThat(entity2.getOrderIndex()).isNotEqualTo(originalIndex2);
    }

    @Test
    @Disabled
    void getIndexAfter_매우_큰_값에서_오버플로우_예외_발생() {
        // given

        // when & then

    }

    @Test
    void getIndexBetweenValues_연속된_MIN_INCREMENT_값들_사이에서_예외_발생() {
        // given
        BigDecimal prev = BigDecimal.valueOf(1024.001);
        BigDecimal next = BigDecimal.valueOf(1024.002); // 차이가 정확히 MIN_INCREMENT = 0.001

        // when & then
        assertThatThrownBy(() -> fractionalOrderManager.generateOrderIndexBetween(prev, next))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("인덱스 변경 한계에 도달하였습니다.");
    }

    private static class TestOrderableEntity implements Orderable {
        private BigDecimal orderIndex;

        public TestOrderableEntity(final BigDecimal orderIndex) {
            this.orderIndex = orderIndex;
        }

        @Override
        public BigDecimal getOrderIndex() {
            return orderIndex;
        }

        @Override
        public void updateOrderIndex(BigDecimal orderIndex) {
            this.orderIndex = orderIndex;
        }
    }
}