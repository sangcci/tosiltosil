package tosiltosil.backend.common.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tosiltosil.backend.common.domain.exception.BadRequestException;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class FractionalOrderManagerUnitTest {

    private final FractionalOrderManager fractionalOrderManager = new FractionalOrderManager();

    @Nested
    class 순서_인덱스_생성_기능 {

        @Nested
        class 마지막_인덱스가_null인_경우 {

            @Test
            void 기본_인덱스_값을_반환한다() {
                // when
                BigDecimal result = fractionalOrderManager.generateOrderIndex(null);

                // then
                assertThat(result).isEqualTo(BigDecimal.valueOf(1024));
            }
        }

        @Nested
        class 마지막_인덱스가_있는_경우 {

            @Test
            void 마지막_인덱스에_INCREMENT를_더한_값을_반환한다() {
                // given
                BigDecimal lastOrderIndex = BigDecimal.valueOf(1024);

                // when
                BigDecimal result = fractionalOrderManager.generateOrderIndex(lastOrderIndex);

                // then
                assertThat(result).isEqualTo(BigDecimal.valueOf(2048));
            }
        }
    }

    @Nested
    class 연속_순서_인덱스_생성_기능 {

        @Nested
        class 카운트가_0_이하인_경우 {

            @Test
            void 빈_리스트를_반환한다() {
                // when
                List<BigDecimal> result = fractionalOrderManager.generateSequentialOrderIndexes(null, 0);

                // then
                assertThat(result).isEmpty();
            }
        }

        @Nested
        class 마지막_인덱스가_null인_경우 {

            @Test
            void 첫_번째_인덱스부터_연속적으로_생성한다() {
                // when
                List<BigDecimal> result = fractionalOrderManager.generateSequentialOrderIndexes(null, 3);

                // then
                assertThat(result).hasSize(3);
                assertThat(result.get(0)).isEqualTo(BigDecimal.valueOf(2048)); // 1024 + 1024
                assertThat(result.get(1)).isEqualTo(BigDecimal.valueOf(3072)); // 2048 + 1024
                assertThat(result.get(2)).isEqualTo(BigDecimal.valueOf(4096)); // 3072 + 1024
            }

            @Test
            void 단일_인덱스_생성() {
                // when
                List<BigDecimal> result = fractionalOrderManager.generateSequentialOrderIndexes(null, 1);

                // then
                assertThat(result).hasSize(1);
                assertThat(result.get(0)).isEqualTo(BigDecimal.valueOf(2048));
            }
        }

        @Nested
        class 마지막_인덱스가_있는_경우 {

            @Test
            void 마지막_인덱스_기준으로_연속적으로_생성한다() {
                // given
                BigDecimal lastOrderIndex = BigDecimal.valueOf(1024);

                // when
                List<BigDecimal> result = fractionalOrderManager.generateSequentialOrderIndexes(lastOrderIndex, 3);

                // then
                assertThat(result).hasSize(3);
                assertThat(result.get(0)).isEqualTo(BigDecimal.valueOf(2048)); // 1024 + 1024
                assertThat(result.get(1)).isEqualTo(BigDecimal.valueOf(3072)); // 2048 + 1024
                assertThat(result.get(2)).isEqualTo(BigDecimal.valueOf(4096)); // 3072 + 1024
            }

            @Test
            void 소수점_값에서도_정확히_연속_생성한다() {
                // given
                BigDecimal lastOrderIndex = BigDecimal.valueOf(512.5);

                // when
                List<BigDecimal> result = fractionalOrderManager.generateSequentialOrderIndexes(lastOrderIndex, 2);

                // then
                assertThat(result).hasSize(2);
                assertThat(result.get(0)).isEqualTo(BigDecimal.valueOf(1536.5)); // 512.5 + 1024
                assertThat(result.get(1)).isEqualTo(BigDecimal.valueOf(2560.5)); // 1536.5 + 1024
            }
        }
    }

    @Nested
    class 위치별_순서_인덱스_계산_기능 {

        @Nested
        class 타겟_포지션_검증 {

            @Test
            void 타겟_포지션이_1보다_작으면_예외를_발생시킨다() {
                // given
                List<TestOrderableEntity> entities = List.of(
                        new TestOrderableEntity(BigDecimal.valueOf(1024))
                );

                // when & then
                assertThatThrownBy(() -> fractionalOrderManager.calculateOrderIndexForPosition(entities, 0))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("타겟 포지션 정보가 올바르지 않습니다.");
            }

            @Test
            void 타겟_포지션이_엔티티_범위보다_크면_예외를_발생시킨다() {
                // given
                List<TestOrderableEntity> entities = List.of(
                        new TestOrderableEntity(BigDecimal.valueOf(1024)),
                        new TestOrderableEntity(BigDecimal.valueOf(2048))
                );

                // when & then
                assertThatThrownBy(() -> fractionalOrderManager.calculateOrderIndexForPosition(entities, 4))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage("타겟 포지션 정보가 올바르지 않습니다.");
            }
        }

        @Nested
        class 첫_번째_위치로_이동 {

            @Test
            void 단일_엔티티_리스트에서_첫_번째_위치의_인덱스를_계산한다() {
                // given
                List<TestOrderableEntity> entities = List.of(
                        new TestOrderableEntity(BigDecimal.valueOf(2048))
                );

                // when
                BigDecimal result = fractionalOrderManager.calculateOrderIndexForPosition(entities, 1);

                // then
                assertThat(result).isEqualTo(BigDecimal.valueOf(1024)); // 2048 / 2
            }

            @Test
            void 여러_엔티티_리스트에서_첫_번째_위치의_인덱스를_계산한다() {
                // given
                List<TestOrderableEntity> entities = List.of(
                        new TestOrderableEntity(BigDecimal.valueOf(1024)),
                        new TestOrderableEntity(BigDecimal.valueOf(2048)),
                        new TestOrderableEntity(BigDecimal.valueOf(3072))
                );

                // when
                BigDecimal result = fractionalOrderManager.calculateOrderIndexForPosition(entities, 1);

                // then
                assertThat(result).isEqualTo(BigDecimal.valueOf(512)); // 1024 / 2
            }
        }

        @Nested
        class 중간_위치로_이동 {

            @Test
            void 두_엔티티_사이의_중간_인덱스를_계산한다() {
                // given
                List<TestOrderableEntity> entities = List.of(
                        new TestOrderableEntity(BigDecimal.valueOf(1024)),
                        new TestOrderableEntity(BigDecimal.valueOf(3072))
                );

                // when
                BigDecimal result = fractionalOrderManager.calculateOrderIndexForPosition(entities, 2);

                // then
                assertThat(result).isEqualTo(BigDecimal.valueOf(2048)); // (1024 + 3072) / 2
            }

            @Test
            void 여러_엔티티_중간에_삽입할_인덱스를_계산한다() {
                // given
                List<TestOrderableEntity> entities = List.of(
                        new TestOrderableEntity(BigDecimal.valueOf(1024)),
                        new TestOrderableEntity(BigDecimal.valueOf(2048)),
                        new TestOrderableEntity(BigDecimal.valueOf(4096))
                );

                // when
                BigDecimal result = fractionalOrderManager.calculateOrderIndexForPosition(entities, 2);

                // then
                assertThat(result).isEqualTo(BigDecimal.valueOf(1536)); // (1024 + 2048) / 2
            }
        }

        @Nested
        class 마지막_위치로_이동 {

            @Test
            void 마지막_위치에_추가할_인덱스를_계산한다() {
                // given
                List<TestOrderableEntity> entities = List.of(
                        new TestOrderableEntity(BigDecimal.valueOf(1024)),
                        new TestOrderableEntity(BigDecimal.valueOf(2048))
                );

                // when
                BigDecimal result = fractionalOrderManager.calculateOrderIndexForPosition(entities, 3);

                // then
                assertThat(result).isEqualTo(BigDecimal.valueOf(3072)); // 2048 + 1024
            }
        }

        @Nested
        class 재정렬_필요한_경우 {

            @Test
            void 인덱스가_최소값_이하일_때_재정렬_후_계산한다() {
                // given
                List<TestOrderableEntity> entities = new ArrayList<>();
                entities.add(new TestOrderableEntity(BigDecimal.valueOf(0.0005))); // MIN_INDEX보다 작음
                entities.add(new TestOrderableEntity(BigDecimal.valueOf(0.001)));

                // when
                BigDecimal result = fractionalOrderManager.calculateOrderIndexForPosition(entities, 1);

                // then
                // 재정렬 후 첫 번째 엔티티는 1024, 두 번째는 2048이 됨
                assertThat(result).isEqualTo(BigDecimal.valueOf(512)); // 1024 / 2
                assertThat(entities.get(0).getOrderIndex()).isEqualTo(BigDecimal.valueOf(1024));
                assertThat(entities.get(1).getOrderIndex()).isEqualTo(BigDecimal.valueOf(2048));
            }

            @Test
            void 재정렬_후_중간_위치_인덱스를_계산한다() {
                // given
                List<TestOrderableEntity> entities = new ArrayList<>();
                entities.add(new TestOrderableEntity(BigDecimal.valueOf(0.0005)));
                entities.add(new TestOrderableEntity(BigDecimal.valueOf(0.001)));
                entities.add(new TestOrderableEntity(BigDecimal.valueOf(0.002)));

                // when
                BigDecimal result = fractionalOrderManager.calculateOrderIndexForPosition(entities, 2);

                // then
                // 재정렬 후: [1024, 2048, 3072]
                assertThat(result).isEqualTo(BigDecimal.valueOf(1536)); // (1024 + 2048) / 2
                assertThat(entities.get(0).getOrderIndex()).isEqualTo(BigDecimal.valueOf(1024));
                assertThat(entities.get(1).getOrderIndex()).isEqualTo(BigDecimal.valueOf(2048));
                assertThat(entities.get(2).getOrderIndex()).isEqualTo(BigDecimal.valueOf(3072));
            }
        }
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