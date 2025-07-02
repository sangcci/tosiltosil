package tosiltosil.backend.common.domain.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Duration;
import java.time.format.DateTimeParseException;

public class DurationValidator implements ConstraintValidator<IsDuration, String> {

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        try {
            Duration.parse(value);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
