package tosiltosil.backend.common.domain.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Component;
import tosiltosil.backend.common.domain.exception.BadRequestException;

@Component
public class FractionalOrderManager implements OrderManager {

    private static final BigDecimal INITIAL_INDEX = BigDecimal.valueOf(1024);
    private static final BigDecimal MIN_INCREMENT = BigDecimal.valueOf(0.001);
    private static final int SCALE = 3;

    @Override
    public BigDecimal generateInitialOrderIndex() {
        return INITIAL_INDEX;
    }

    @Override
    public BigDecimal generateOrderIndexBetween(
            final BigDecimal prevOrderIndex,
            final BigDecimal nextOrderIndex
    ) {
        if (prevOrderIndex == null && nextOrderIndex == null) {
            return INITIAL_INDEX;
        }

        if (prevOrderIndex == null) {
            return getIndexBefore(nextOrderIndex);
        }

        if (nextOrderIndex == null) {
            return getIndexAfter(prevOrderIndex);
        }

        return getIndexBetweenValues(prevOrderIndex, nextOrderIndex);
    }

    @Override
    public <T extends Orderable> List<T> renewOrderIndexes(final List<T> entities) {
        BigDecimal startIndex = INITIAL_INDEX;
        BigDecimal increment = INITIAL_INDEX;

        for (int i = 0; i < entities.size(); i++) {
            BigDecimal newOrderIndex = startIndex.add(BigDecimal.valueOf(i).multiply(increment));
            entities.get(i).updateOrderIndex(newOrderIndex);
        }

        return entities;
    }

    private BigDecimal getIndexBefore(final BigDecimal index) {
        BigDecimal currentIndex = index;
        BigDecimal beforeIndex = currentIndex.subtract(INITIAL_INDEX);

        // If subtraction results in negative or too small value, try division by 2
        if (beforeIndex.compareTo(BigDecimal.ZERO) < 0 || beforeIndex.compareTo(MIN_INCREMENT) < 0) {
            beforeIndex = currentIndex.divide(BigDecimal.valueOf(2), SCALE, RoundingMode.HALF_UP);
        }

        // Check if the result is still too small (precision limit reached)
        if (beforeIndex.compareTo(MIN_INCREMENT) < 0) {
            throw new BadRequestException("인덱스 변경 한계에 도달하였습니다.");
        }

        return beforeIndex;
    }

    private BigDecimal getIndexAfter(final BigDecimal index) {
        return index.add(INITIAL_INDEX);
    }

    private BigDecimal getIndexBetweenValues(final BigDecimal prevIndex, final BigDecimal nextIndex) {
        BigDecimal difference = nextIndex.subtract(prevIndex);
        if (difference.compareTo(MIN_INCREMENT) <= 0) {
            throw new BadRequestException("인덱스 변경 한계에 도달하였습니다.");
        }

        return prevIndex.add(difference.divide(BigDecimal.valueOf(2), SCALE, RoundingMode.HALF_UP));
    }
}
