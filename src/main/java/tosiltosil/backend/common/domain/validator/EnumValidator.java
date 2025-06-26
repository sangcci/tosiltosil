package tosiltosil.backend.common.domain.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;

public class EnumValidator implements ConstraintValidator<IsEnum, String> {

    private Class<? extends java.lang.Enum<?>> enumClass;

    @Override
    public void initialize(final IsEnum constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        // 입력 값이 null 혹은 blank일 경우
        if (value == null || value.isBlank()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("빈 값이 올 수 없습니다. 값을 입력해주세요.")
                    .addConstraintViolation();
            return false;
        }
        // Enum Class 내 상수 값 추출
        final Object[] enumConstants = this.enumClass.getEnumConstants();
        // 입력 값이 Enum 상수와 똑같은지 비교
        for (Object enumConstant : enumConstants) {
            if (Objects.equals(enumConstant.toString(), value)) {
                return true;
            }
        }
        return false;
    }
}
