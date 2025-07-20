package tosiltosil.backend.common.domain.holder;

import java.time.LocalDate;

public class TestTimeHolder implements TimeHolder {

    private final LocalDate fixedDate;

    public TestTimeHolder(final LocalDate fixedDate) {
        this.fixedDate = fixedDate;
    }

    @Override
    public LocalDate getCurrentDate() {
        return fixedDate;
    }
}