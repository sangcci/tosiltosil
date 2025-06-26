package tosiltosil.backend.common.domain.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateValidator implements ConstraintValidator<IsDate, String> {

    private String pattern;

    @Override
    public void initialize(final IsDate constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        try {
            LocalDate.parse(value, DateTimeFormatter.ofPattern(this.pattern));
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
}
