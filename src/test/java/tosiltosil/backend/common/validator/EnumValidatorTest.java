package tosiltosil.backend.common.validator;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import tosiltosil.backend.common.domain.validator.EnumValidator;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class EnumValidatorTest {

    private EnumValidator enumValidator;
    private ConstraintValidatorContext contextMock;
    private ConstraintViolationBuilder builderMock;

    @BeforeEach
    void setUp() {
        enumValidator = new EnumValidator();
        ReflectionTestUtils.setField(enumValidator, "enumClass", TestEnum.class);
        contextMock = mock(ConstraintValidatorContext.class);
        builderMock = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
    }

    enum TestEnum {
        VALUE
    }

    @Test
    void 올바른_Enum값이_들어왔다면_true를_반환한다() {
        // Given
        String value = "VALUE";

        // When
        boolean result = enumValidator.isValid(value, contextMock);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void 잘못된_입력값이_주어질_경우_false를_반환한다() {
        // Given
        String value = "INVALID";

        // When
        boolean result = enumValidator.isValid(value, contextMock);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void 입력값이_비어있을경우_false를_반환한다() {
        // Given
        String value = "";
        when(contextMock.buildConstraintViolationWithTemplate(anyString())).thenReturn(builderMock);

        // When
        boolean result = enumValidator.isValid(value, contextMock);

        // Then
        assertThat(result).isFalse();
        verify(contextMock).disableDefaultConstraintViolation();
        verify(contextMock).buildConstraintViolationWithTemplate("빈 값이 올 수 없습니다. 값을 입력해주세요.");
    }

    @Test
    void 입력_값이_null이라면_false를_반환한다() {
        // Given
        String value = null;
        when(contextMock.buildConstraintViolationWithTemplate(anyString())).thenReturn(builderMock);

        // When
        boolean result = enumValidator.isValid(value, contextMock);

        // Then
        assertThat(result).isFalse();
        verify(contextMock).disableDefaultConstraintViolation();
        verify(contextMock).buildConstraintViolationWithTemplate("빈 값이 올 수 없습니다. 값을 입력해주세요.");
    }
}