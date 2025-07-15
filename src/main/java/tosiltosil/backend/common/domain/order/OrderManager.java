package tosiltosil.backend.common.domain.order;

import java.util.List;

public interface OrderManager {

    Double generateInitialOrderIndex();

    Double generateOrderIndexBetween(Double prevOrderIndex, Double nextOrderIndex);

    <T extends Orderable> List<T> renewOrderIndexes(List<T> entities);
}