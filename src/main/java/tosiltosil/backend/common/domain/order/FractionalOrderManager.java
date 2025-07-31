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

    @Override
    public BigDecimal generateInitialOrderIndex() {
        return INCREMENT;
    }

    @Override
    public BigDecimal generateOrderIndexBetween(
            final BigDecimal prevOrderIndex,
            final BigDecimal nextOrderIndex
    ) {
        // 처음 개체를 만들 경우
        if (prevOrderIndex == null && nextOrderIndex == null) {
            return INCREMENT;
        }

        // 맨 처음 순서로 옮길 경우
        if (prevOrderIndex == null) {
            return getIndexBefore(nextOrderIndex);
        }

        // 맨 마지막 순서로 옮길 경우
        if (nextOrderIndex == null) {
            return getIndexAfter(prevOrderIndex);
        }

        return getIndexBetweenValues(prevOrderIndex, nextOrderIndex);
    }

    @Override
    public boolean validateIndexBounds(final BigDecimal prevIndex, final BigDecimal nextIndex) {
        if (prevIndex != null && prevIndex.compareTo(MIN_INDEX) <= 0) {
            return false;
        }
        if (nextIndex != null && nextIndex.add(INCREMENT).compareTo(MAX_INDEX) > 0) {
            return false;
        }
        if (nextIndex != null && nextIndex.compareTo(MIN_INDEX) <= 0) {
            return false;
        }
        return true;
    }

    @Override
    public <T extends Orderable> List<T> renewOrderIndexes(final List<T> entities) {
        BigDecimal startIndex = INCREMENT;

        for (int i = 0; i < entities.size(); i++) {
            BigDecimal newOrderIndex = startIndex.add(BigDecimal.valueOf(i).multiply(INCREMENT));
            entities.get(i).updateOrderIndex(newOrderIndex);
        }

        return entities;
    }

    @Override
    public <T extends Orderable> BigDecimal calculateOrderIndexForPosition(
            final List<T> entities,
            final int targetPosition
    ) {
        // targetPosition이 유효한 범위인지 확인
        if (targetPosition < 1 || targetPosition > entities.size()) {
            throw new BadRequestException("타겟 포지션 정보가 올바르지 않습니다.");
        }

        // prevOrderIndex와 nextOrderIndex 계산
        IndexBoundary boundary = calculateBoundaryIndexes(entities, targetPosition);

        // 인덱스 재정렬이 필요한지 확인
        if (!validateIndexBounds(boundary.prevOrderIndex(), boundary.nextOrderIndex())) {
            renewOrderIndexes(entities);
            // 재정렬 후 다시 인덱스 계산
            boundary = calculateBoundaryIndexes(entities, targetPosition);
        }

        return generateOrderIndexBetween(boundary.prevOrderIndex(), boundary.nextOrderIndex());
    }

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

    @Override
    public List<BigDecimal> generateSequentialOrderIndexes(final BigDecimal lastOrderIndex, final int count) {
        if (count <= 0) {
            return List.of();
        }

        List<BigDecimal> indexes = new ArrayList<>();
        BigDecimal currentIndex = lastOrderIndex != null ? lastOrderIndex : generateInitialOrderIndex();

        for (int i = 0; i < count; i++) {
            currentIndex = generateOrderIndexBetween(currentIndex, null);
            indexes.add(currentIndex);
        }

        return indexes;
    }

    private BigDecimal getIndexBefore(final BigDecimal nextIndex) {
        return nextIndex.divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
    }

    private BigDecimal getIndexAfter(final BigDecimal prevIndex) {
        return prevIndex.add(INCREMENT);
    }

    private BigDecimal getIndexBetweenValues(final BigDecimal prevIndex, final BigDecimal nextIndex) {
        BigDecimal difference = nextIndex.subtract(prevIndex);
        return prevIndex.add(difference.divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP));
    }
}
