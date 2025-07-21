package tosiltosil.backend.common.domain.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
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
    void 이전_인덱스가_null인_경우_다음_인덱스를_반으로_나누기() {
        // given
        BigDecimal nextIndex = BigDecimal.valueOf(1024);

        // when
        BigDecimal index = fractionalOrderManager.generateOrderIndexBetween(null, nextIndex);

        // then
        assertThat(index).isEqualTo(BigDecimal.valueOf(512)); // 1024 / 2 = 512
    }

    @Test
    void 다음_인덱스가_null인_경우_이전_인덱스에_INCREMENT_더하기() {
        // given
        BigDecimal prevIndex = BigDecimal.valueOf(1024);

        // when
        BigDecimal index = fractionalOrderManager.generateOrderIndexBetween(prevIndex, null);

        // then
        assertThat(index).isEqualTo(BigDecimal.valueOf(2048)); // 1024 + 1024 = 2048
    }

    @Test
    void 두_인덱스_사이의_중간값_계산() {
        // given
        BigDecimal prevIndex = BigDecimal.valueOf(1024);
        BigDecimal nextIndex = BigDecimal.valueOf(3072);

        // when
        BigDecimal index = fractionalOrderManager.generateOrderIndexBetween(prevIndex, nextIndex);

        // then
        assertThat(index).isEqualTo(BigDecimal.valueOf(2048)); // (1024 + 3072) / 2 = 2048
        assertThat(index).isGreaterThan(prevIndex);
        assertThat(index).isLessThan(nextIndex);
    }

    @Test
    void 두_인덱스_사이의_중간값_계산_소수점_포함() {
        // given
        BigDecimal prevIndex = BigDecimal.valueOf(1024);
        BigDecimal nextIndex = BigDecimal.valueOf(2048);

        // when
        BigDecimal index = fractionalOrderManager.generateOrderIndexBetween(prevIndex, nextIndex);

        // then
        assertThat(index).isEqualTo(BigDecimal.valueOf(1536)); // (1024 + 2048) / 2 = 1536
        assertThat(index).isGreaterThan(prevIndex);
        assertThat(index).isLessThan(nextIndex);
    }

    @Test
    void 이전_인덱스가_MIN_INDEX_이하인_경우_false() {
        // given
        BigDecimal prevIndex = BigDecimal.valueOf(0.001);
        BigDecimal nextIndex = BigDecimal.valueOf(2048);

        // when
        boolean result = fractionalOrderManager.validateIndexBounds(prevIndex, nextIndex);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 다음_인덱스_더하기_INCREMENT가_MAX_INDEX_초과인_경우_false() {
        // given
        BigDecimal prevIndex = BigDecimal.valueOf(1024);
        BigDecimal nextIndex = BigDecimal.valueOf(999000); // 999000 + 1024 = 1000024 > 1000000

        // when
        boolean result = fractionalOrderManager.validateIndexBounds(prevIndex, nextIndex);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 다음_인덱스가_MIN_INDEX_이하인_경우_false() {
        // given
        BigDecimal nextIndex = BigDecimal.valueOf(0.001); // MIN_INDEX

        // when
        boolean result = fractionalOrderManager.validateIndexBounds(null, nextIndex);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 엔티티들의_순서_인덱스_갱신() {
        // given
        TestOrderableEntity entity1 = new TestOrderableEntity(BigDecimal.valueOf(1536));
        TestOrderableEntity entity2 = new TestOrderableEntity(BigDecimal.valueOf(3000));
        TestOrderableEntity entity3 = new TestOrderableEntity(BigDecimal.valueOf(500));
        List<TestOrderableEntity> entities = List.of(entity1, entity2, entity3);

        // when
        List<TestOrderableEntity> result = fractionalOrderManager.renewOrderIndexes(entities);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getOrderIndex()).isEqualTo(BigDecimal.valueOf(1024)); // 1024 + (0 * 1024)
        assertThat(result.get(1).getOrderIndex()).isEqualTo(BigDecimal.valueOf(2048)); // 1024 + (1 * 1024)
        assertThat(result.get(2).getOrderIndex()).isEqualTo(BigDecimal.valueOf(3072)); // 1024 + (2 * 1024)
    }

    @Test
    void 빈_리스트인_경우() {
        // given
        List<TestOrderableEntity> entities = new ArrayList<>();

        // when
        List<TestOrderableEntity> result = fractionalOrderManager.renewOrderIndexes(entities);

        // then
        assertThat(result).isEmpty();
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