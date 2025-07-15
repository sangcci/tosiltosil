package tosiltosil.backend.common.domain.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class FractionalOrderManager implements OrderManager {

    private static final Double INITIAL_INDEX = 1000000.0;
    private static final Double MIN_INCREMENT = 0.000001;
    private static final int SCALE = 6;

    @Override
    public Double generateInitialOrderIndex() {
        return INITIAL_INDEX;
    }

    @Override
    public Double generateOrderIndexBetween(Double prevOrderIndex, Double nextOrderIndex) {
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

    public Double getIndexBefore(final Double index) {
        if (index == null) {
            return INITIAL_INDEX;
        }

        BigDecimal currentIndex = BigDecimal.valueOf(index);
        BigDecimal beforeIndex = currentIndex.subtract(BigDecimal.valueOf(INITIAL_INDEX));

        if (beforeIndex.compareTo(BigDecimal.valueOf(MIN_INCREMENT)) < 0) {
            beforeIndex = currentIndex.divide(BigDecimal.valueOf(2), SCALE, RoundingMode.HALF_UP);
        }

        return beforeIndex.doubleValue();
    }

    public Double getIndexAfter(final Double index) {
        if (index == null) {
            return INITIAL_INDEX;
        }

        BigDecimal currentIndex = BigDecimal.valueOf(index);
        BigDecimal afterIndex = currentIndex.add(BigDecimal.valueOf(INITIAL_INDEX));

        return afterIndex.doubleValue();
    }

    private Double getIndexBetweenValues(final Double prevIndex, final Double nextIndex) {
        BigDecimal prev = BigDecimal.valueOf(prevIndex);
        BigDecimal next = BigDecimal.valueOf(nextIndex);

        if (prev.compareTo(next) >= 0) {
            throw new IllegalArgumentException("Previous index must be less than next index");
        }

        BigDecimal difference = next.subtract(prev);
        if (difference.compareTo(BigDecimal.valueOf(MIN_INCREMENT)) <= 0) {
            throw new IllegalArgumentException("Indexes are too close to insert between");
        }

        BigDecimal midpoint = prev.add(difference.divide(BigDecimal.valueOf(2), SCALE, RoundingMode.HALF_UP));

        return midpoint.doubleValue();
    }
}
