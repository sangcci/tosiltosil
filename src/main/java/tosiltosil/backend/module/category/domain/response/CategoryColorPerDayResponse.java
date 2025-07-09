package tosiltosil.backend.module.category.domain.response;

import tosiltosil.backend.module.category.domain.Category;

public record CategoryColorPerDayResponse(
        //LocalDate date,
        String color
) {

    public static CategoryColorPerDayResponse of(final Category category) {
        return new CategoryColorPerDayResponse(category.getColor());
    }
}
