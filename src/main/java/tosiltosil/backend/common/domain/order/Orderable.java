package tosiltosil.backend.common.domain.order;

public interface Orderable {

    String getOrderKey();

    void updateOrderKey(String orderKey);
}