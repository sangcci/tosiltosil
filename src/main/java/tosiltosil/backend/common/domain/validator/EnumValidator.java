package tosiltosil.backend.common.domain.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<Enum, java.lang.Enum<?>> {

    @Override
    public boolean isValid(final java.lang.Enum<?> value, final ConstraintValidatorContext context) {
        // EnumDeserializer가 이미 Enum을 검증한 후 매핑하였기 때문에, value가 null인지만 판단하면 됨.
        return value != null;
    }
}
