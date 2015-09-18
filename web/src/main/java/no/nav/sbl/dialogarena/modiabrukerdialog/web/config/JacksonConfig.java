package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import no.nav.modig.lang.option.Optional;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class JacksonConfig implements ContextResolver<ObjectMapper> {
    private ObjectMapper mapper;

    public JacksonConfig() {
        mapper = new ObjectMapper();
        mapper.registerModule(new OptionalSerializerModule());
        mapper.registerModule(new JodaModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }

    public static class OptionalSerializerModule extends SimpleModule {

        public OptionalSerializerModule() {
            super(Optional.class.getName());
        }

        @Override
        public void setupModule(SetupContext context) {
            context.addSerializers(new OptionalSerializers());
            context.addDeserializers(new OptionalDeserializers());

            super.setupModule(context);
        }

        static class OptionalSerializers extends Serializers.Base {
            @Override
            public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
                Class<?> raw = type.getRawClass();
                if (Optional.class.isAssignableFrom(raw)) {
                    return new OptionalSerializer(type);
                }
                return super.findSerializer(config, type, beanDesc);
            }
        }

        static class OptionalDeserializers extends Deserializers.Base {
            @Override
            public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
                Class<?> raw = type.getRawClass();
                if (Optional.class.isAssignableFrom(raw)) {
                    return new JavaOptionalDeserializer(type);
                }
                return super.findBeanDeserializer(type, config, beanDesc);
            }
        }

        static class OptionalSerializer extends StdSerializer<Optional<?>> {

            public OptionalSerializer(JavaType type) {
                super(type);
            }

            @Override
            public void serialize(Optional<?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
                if (value.isSome()) {
                    provider.defaultSerializeValue(value.get(), jgen);
                } else {
                    provider.defaultSerializeNull(jgen);
                }
            }
        }

        static class JavaOptionalDeserializer extends StdDeserializer<Optional<?>> {
            private final JavaType innerType;

            public JavaOptionalDeserializer(JavaType type) {
                super(type);
                this.innerType = type.containedType(0);
            }

            @Override
            public Optional<?> getNullValue() {
                return Optional.none();
            }

            @Override
            public Optional<?> deserialize(JsonParser jp, DeserializationContext context) throws IOException, JsonProcessingException {
                Object reference = context.findRootValueDeserializer(innerType).deserialize(jp, context);
                return Optional.optional(reference);
            }
        }
    }

}
