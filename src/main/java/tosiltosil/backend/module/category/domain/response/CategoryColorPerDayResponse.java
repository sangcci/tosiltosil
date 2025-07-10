package tosiltosil.backend.module.category.domain.response;

import java.time.LocalDate;
import java.util.List;

public record CategoryColorPerDayResponse(
        LocalDate date,
        List<String> color
) {

}
