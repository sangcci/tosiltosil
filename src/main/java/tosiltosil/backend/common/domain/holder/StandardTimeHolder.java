package tosiltosil.backend.common.domain.holder;

import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class StandardTimeHolder implements TimeHolder {

    @Override
    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }
}
