package tosiltosil.backend.module.category.domain.response;

import java.time.LocalDate;
import tosiltosil.backend.module.category.domain.Category;

public record CategoryColorPerDayResponse(
        LocalDate date,
        String color
) {

    public static CategoryColorPerDayResponse of(final Category category) {
        return new CategoryColorPerDayResponse(category.getDate(), category.getColor());
    }
}
