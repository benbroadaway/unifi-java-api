package org.benbroadaway.unifi.actions;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import java.io.IOException;

public class Util {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new Jdk8Module());

    private Util() {
        // no instantiation
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    public static <T> T withMapper(MapperFunction<T> f) throws IOException {
        return f.apply(MAPPER);
    }

    @FunctionalInterface
    public interface MapperFunction<T> {
        T apply(ObjectMapper mapper) throws IOException;
    }
}
