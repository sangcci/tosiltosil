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

public class EnumDeserializer extends StdDeserializer<Enum<?>> implements ContextualDeserializer {

    public EnumDeserializer() {
        this(null);
    }

    protected EnumDeserializer(final Class<?> vc) {
        super(vc);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Enum<?> deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        // Json으로부터 value 값 가져옴
        JsonNode jsonNode = jp.getCodec().readTree(jp);
        String text = jsonNode.asText();

        // 어노테이션과 연결된 Enum 타입으로부터 value 값 가져옴
        Class<? extends Enum> enumType = (Class<? extends Enum>) this._valueClass;

        // 비교
        return Arrays.stream(enumType.getEnumConstants())
                .filter(constant -> constant.name().equals(text))
                .findAny()
                .orElse(null);
    }

    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        return new EnumDeserializer(property.getType().getRawClass());
    }
}