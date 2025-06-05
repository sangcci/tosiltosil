package tosiltosil.backend.common.domain.exception;

import java.util.Arrays;
import lombok.Getter;

@Getter
public class InvalidEnumValueException extends RuntimeException {
    private final String fieldName;
    private final String rejectedValue;
    private final String[] validValues;

    public InvalidEnumValueException(
            final String fieldName,
            final String rejectedValue,
            final String[] validValues
    ) {
        super(String.format("유효하지 않은 값입니다. 가능한 값: %s", Arrays.toString(validValues)));
        this.fieldName = fieldName;
        this.rejectedValue = rejectedValue;
        this.validValues = validValues;
    }
}
