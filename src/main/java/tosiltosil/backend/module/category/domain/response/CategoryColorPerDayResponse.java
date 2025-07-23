package tosiltosil.backend.module.category.domain.response;

import tosiltosil.backend.module.category.domain.value.CategoryColor;

import java.time.LocalDate;
import java.util.List;

public record CategoryColorPerDayResponse(
        LocalDate date,
        List<CategoryColor> color
) {

}
