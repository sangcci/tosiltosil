package tosiltosil.backend.common.domain.order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderManager {

    BigDecimal generateInitialOrderIndex();

    BigDecimal generateOrderIndexBetween(BigDecimal prevOrderIndex, BigDecimal nextOrderIndex);

    <T extends Orderable> List<T> renewOrderIndexes(List<T> entities);
}