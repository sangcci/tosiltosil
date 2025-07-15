package tosiltosil.backend.common.domain.order;

import java.math.BigDecimal;

public interface Orderable {

    BigDecimal getOrderIndex();
    void updateOrderIndex(BigDecimal orderIndex);
}
