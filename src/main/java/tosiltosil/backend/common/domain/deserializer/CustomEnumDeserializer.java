package tosiltosil.backend.common.domain.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.Arrays;
import tosiltosil.backend.common.domain.exception.InvalidEnumValueException;

public class CustomEnumDeserializer extends StdDeserializer<Enum<?>> implements ContextualDeserializer {

    private final String fieldName; // 필드명 저장

    public CustomEnumDeserializer() {
        this(null, null);
    }

    protected CustomEnumDeserializer(final Class<?> vc, final String fieldName) {
        super(vc);
        this.fieldName = fieldName;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Enum<?> deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        // Json으로부터 value 값 가져옴
        JsonNode jsonNode = jp.getCodec().readTree(jp);
        String text = jsonNode.asText();

        // 어노테이션과 연결된 Enum 타입으로부터 value 값 가져옴
        Class<? extends Enum> enumType = (Class<? extends Enum>) this._valueClass;
        String[] validValues = Arrays.stream(enumType.getEnumConstants())
                .map(Enum::name)
                .toArray(String[]::new);

        // 비교
        return Arrays.stream(enumType.getEnumConstants())
                .filter(constant -> constant.name().equals(text))
                .findAny()
                .orElseThrow(() -> new InvalidEnumValueException(
                        this.fieldName,
                        text,
                        validValues
                ));
    }

    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        String propertyName = property != null ? property.getName() : "unknown";
        return new CustomEnumDeserializer(property.getType().getRawClass(), propertyName);
    }
}