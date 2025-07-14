package tosiltosil.backend.common.domain.order;

import org.springframework.stereotype.Component;

@Component
public class OrderManager {

    public String generateInitialOrderKey() {
        return LexoRank.getInitialRank();
    }

    public String generateOrderKeyBetween(final String prevOrderKey, final String nextOrderKey) {
        return LexoRank.getRankBetween(prevOrderKey, nextOrderKey);
    }
}