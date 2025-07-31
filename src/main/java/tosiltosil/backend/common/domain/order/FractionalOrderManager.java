package tosiltosil.backend.common.domain.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import tosiltosil.backend.common.domain.exception.BadRequestException;

@Component
public class FractionalOrderManager implements OrderManager {

    private static final BigDecimal INCREMENT = BigDecimal.valueOf(1024);
    private static final BigDecimal MAX_INDEX = BigDecimal.valueOf(1000000);
    private static final BigDecimal MIN_INDEX = BigDecimal.valueOf(0.001);
    private static final BigDecimal DIVISION_FACTOR = BigDecimal.valueOf(2);

    @Override
    public BigDecimal generateOrderIndex(final BigDecimal lastOrderIndex) {
        return generateOrderIndexBetween(lastOrderIndex, null);
    }

    @Override
    public List<BigDecimal> generateSequentialOrderIndexes(final BigDecimal lastOrderIndex, final int count) {
        // 카운트 0일 경우 생성 X
        if (count <= 0) {
            return List.of();
        }

        // 마지막 인덱스가 null일 경우 초기 인덱스 생성
        final List<BigDecimal> indexes = new ArrayList<>();
        BigDecimal currentIndex = lastOrderIndex != null ? lastOrderIndex : generateInitialOrderIndex();

        // count 만큼 순서 인덱스 생성
        for (int i = 0; i < count; i++) {
            currentIndex = generateOrderIndexBetween(currentIndex, null);
            indexes.add(currentIndex);
        }

        return indexes;
    }

    @Override
    public <T extends Orderable> BigDecimal calculateOrderIndexForPosition(
            final List<T> entities,
            final int targetPosition
    ) {
        // 이동하고자 하는 위치가 유효한 범위인지 검증
        validateTargetPosition(entities, targetPosition);

        // 이동하고자 하는 위치 위 아래 index 추출
        IndexBoundary boundary = calculateBoundaryIndexes(entities, targetPosition);

        // 재정렬 필요한지 확인
        if (requiresRebalancing(boundary)) {
            // 재정렬 후 다시 위 아래 index 추출
            renewOrderIndexes(entities);
            boundary = calculateBoundaryIndexes(entities, targetPosition);
        }

        // 위 아래 index 기반으로 순서 인덱스 생성
        return generateOrderIndexBetween(boundary.prevOrderIndex(), boundary.nextOrderIndex());
    }

    /**
     * 새로 생성된 엔티티의 초기 순서 인덱스를 생성합니다.
     *
     * @return 초기 순서 인덱스
     */
    private BigDecimal generateInitialOrderIndex() {
        return INCREMENT;
    }

    /**
     * 두 개의 주어진 순서 인덱스 사이에 위치하는 새로운 순서 인덱스를 생성합니다.
     * 이전 또는 다음 순서 인덱스가 null인 경우, 미리 정의된 증분을 사용하여 다른 인덱스를 기준으로 새 인덱스를 계산합니다.
     * 두 인덱스가 모두 제공된 경우, 두 값의 중간값을 계산합니다.
     *
     * @param prevOrderIndex 이전 순서 인덱스
     * @param nextOrderIndex 다음 순서 인덱스
     * @return 주어진 인덱스 사이의 순서 인덱스
     */
    private BigDecimal generateOrderIndexBetween(
            final BigDecimal prevOrderIndex,
            final BigDecimal nextOrderIndex
    ) {
        if (prevOrderIndex == null && nextOrderIndex == null) {
            return INCREMENT;
        }

        if (prevOrderIndex == null) {
            return calculateIndexBefore(nextOrderIndex);
        }

        if (nextOrderIndex == null) {
            return calculateIndexAfter(prevOrderIndex);
        }

        return calculateIndexBetween(prevOrderIndex, nextOrderIndex);
    }

    // 주어진 인덱스보다 앞선 새로운 인덱스를 계산
    private BigDecimal calculateIndexBefore(final BigDecimal nextIndex) {
        return nextIndex.divide(DIVISION_FACTOR, RoundingMode.HALF_UP);
    }

    // 주어진 인덱스보다 뒤에 오는 새로운 인덱스를 계산
    private BigDecimal calculateIndexAfter(final BigDecimal prevIndex) {
        return prevIndex.add(INCREMENT);
    }

    // 두 인덱스 사이의 중간값을 계산
    private BigDecimal calculateIndexBetween(final BigDecimal prevIndex, final BigDecimal nextIndex) {
        final BigDecimal difference = nextIndex.subtract(prevIndex);
        return prevIndex.add(difference.divide(DIVISION_FACTOR, RoundingMode.HALF_UP));
    }

    /**
     * 타겟 포지션이 유효한 범위에 있는지 검증합니다.
     *
     * @param entities 엔티티 목록
     * @param targetPosition 검증할 타겟 포지션
     * @param <T> 엔티티 타입
     * @throws BadRequestException 타겟 포지션이 유효하지 않은 경우
     */
    private <T extends Orderable> void validateTargetPosition(final List<T> entities, final int targetPosition) {
        if (targetPosition < 1 || targetPosition > entities.size()) {
            throw new BadRequestException("타겟 포지션 정보가 올바르지 않습니다.");
        }
    }

    /**
     * 순서 인덱스 재정렬이 필요한지 확인합니다.
     *
     * @param boundary 인덱스 경계
     * @return 재정렬이 필요하면 true, 그렇지 않으면 false
     */
    private boolean requiresRebalancing(final IndexBoundary boundary) {
        return isOutOfBounds(boundary.prevOrderIndex()) ||
               isOutOfBounds(boundary.nextOrderIndex()) ||
               !hasValidSpacing(boundary.nextOrderIndex());
    }

    //인덱스가 유효한 범위를 벗어났는지 확인
    private boolean isOutOfBounds(final BigDecimal index) {
        return index != null && index.compareTo(MIN_INDEX) <= 0;
    }

    // 다음 인덱스에 충분한 공간이 있는지 확인
    private boolean hasValidSpacing(final BigDecimal nextIndex) {
        return nextIndex == null || nextIndex.add(INCREMENT).compareTo(MAX_INDEX) <= 0;
    }

    /**
     * 순서를 재정렬합니다.
     * - service 코드 내에서 사용할 때 먼저 validation을 거쳐야 합니다.
     * - 수정할 엔티티 수에 따라 쿼리 최적화를 진행하는 것이 좋습니다
     *
     * @param <T>       목록 내 엔티티의 타입으로, {@link Orderable} 인터페이스를 구현해야 합니다.
     * @param entities  순서 인덱스를 업데이트할 엔티티 목록입니다.
     */
    private <T extends Orderable> void renewOrderIndexes(final List<T> entities) {
        for (int i = 0; i < entities.size(); i++) {
            final BigDecimal multiplier = BigDecimal.valueOf(i);
            final BigDecimal newOrderIndex = INCREMENT.add(multiplier.multiply(INCREMENT));
            entities.get(i).updateOrderIndex(newOrderIndex);
        }
    }

    /**
     * 주어진 엔티티 목록에서 특정 위치의 순서 인덱스 경계(이전과 다음)를 계산합니다.
     * 이 경계 인덱스들은 새로운 순서 인덱스를 계산하기 위한 범위를 결정하는 데 사용됩니다.
     *
     * @param <T>            {@link Orderable} 인터페이스를 구현하는 엔티티 타입
     * @param entities       순서가 정렬된 엔티티 목록
     * @param targetPosition 목표 위치 (1부터 시작)
     * @return 이전과 다음 순서 인덱스를 포함하는 {@code IndexBoundary} 객체
     */
    private <T extends Orderable> IndexBoundary calculateBoundaryIndexes(
            final List<T> entities,
            final int targetPosition
    ) {
        BigDecimal prevOrderIndex = null;
        BigDecimal nextOrderIndex = null;

        if (targetPosition > 1) {
            prevOrderIndex = entities.get(targetPosition - 2).getOrderIndex();
        }
        if (targetPosition <= entities.size()) {
            nextOrderIndex = entities.get(targetPosition - 1).getOrderIndex();
        }

        return new IndexBoundary(prevOrderIndex, nextOrderIndex);
    }

    private record IndexBoundary(BigDecimal prevOrderIndex, BigDecimal nextOrderIndex) {}
}
