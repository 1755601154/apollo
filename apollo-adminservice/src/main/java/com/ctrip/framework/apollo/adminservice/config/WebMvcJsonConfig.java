package com.ctrip.framework.apollo.adminservice.config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import springfox.documentation.spring.web.json.Json;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author yuanlei
 * @date 2020-11-03
 */
@Configuration
public class WebMvcJsonConfig {
    @Bean
    GsonHttpMessageConverter gsonHttpMessageConverter() {
        GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
        converter.setGson(
                new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls()
                        .registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory())
                        .registerTypeAdapter(Json.class, new WebMvcJsonConfig.SpringfoxJsonToGsonAdapter())
                        .create());
        return converter;
    }

    private static class SpringfoxJsonToGsonAdapter implements JsonSerializer<Json> {
        @Override
        public JsonElement serialize(Json json, Type type, JsonSerializationContext context) {
            final JsonParser parser = new JsonParser();
            return parser.parse(json.value());
        }
    }

    public class NullStringToEmptyAdapterFactory<T> implements TypeAdapterFactory {
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            Class<T> rawType = (Class<T>) type.getRawType();
            if (rawType != String.class) {
                return null;
            }
            return (TypeAdapter<T>) new StringNullAdapter();
        }
    }

    public class StringNullAdapter extends TypeAdapter<String> {
        @Override
        public String read(JsonReader reader) throws IOException {
            // TODO Auto-generated method stub
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return "";
            }
            return reader.nextString();
        }
        @Override
        public void write(JsonWriter writer, String value) throws IOException {
            // TODO Auto-generated method stub
            if (value == null) {
                writer.nullValue();
                return;
            }
            writer.value(value);
        }
    }
}
