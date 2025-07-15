package tosiltosil.backend.common.domain.order;

public interface OrderManager {

    Double generateInitialOrderIndex();

    Double generateOrderIndexBetween(Double prevOrderIndex, Double nextOrderIndex);
}