package tosiltosil.backend.common.domain.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Component;

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
        if (prevOrderIndex == null && nextOrderIndex == null) {
            return INCREMENT;
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
    public boolean validateIndexBounds(final BigDecimal prevIndex, final BigDecimal nextIndex) {
        if (prevIndex != null && prevIndex.compareTo(MIN_INDEX) <= 0) {
            return false;
        }
        if (nextIndex != null && nextIndex.add(INCREMENT).compareTo(MAX_INDEX) > 0) {
            return false;
        }
        if (nextIndex != null && nextIndex.subtract(INCREMENT).compareTo(BigDecimal.ZERO) <= 0) {
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

    private BigDecimal getIndexBefore(final BigDecimal nextIndex) {
        return nextIndex.subtract(INCREMENT);
    }

    private BigDecimal getIndexAfter(final BigDecimal prevIndex) {
        return prevIndex.add(INCREMENT);
    }

    private BigDecimal getIndexBetweenValues(final BigDecimal prevIndex, final BigDecimal nextIndex) {
        BigDecimal difference = nextIndex.subtract(prevIndex);
        return prevIndex.add(difference.divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP));
    }
}
