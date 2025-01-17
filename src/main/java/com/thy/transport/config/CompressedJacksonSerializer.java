package com.thy.transport.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressedJacksonSerializer implements RedisSerializer<Object> {
    private final ObjectMapper objectMapper;
    private final TypeFactory typeFactory;

    public CompressedJacksonSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.typeFactory = objectMapper.getTypeFactory();
    }

    @Override
    public byte[] serialize(Object value) throws SerializationException {
        if (value == null) {
            return new byte[0];
        }

        try {
            TypeWrapper wrapper = new TypeWrapper(value, objectMapper);
            byte[] serialized = objectMapper.writeValueAsBytes(wrapper);
            return compress(serialized);
        } catch (IOException e) {
            throw new SerializationException("Failed to serialize value", e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        try {
            byte[] decompressed = decompress(bytes);
            TypeWrapper wrapper = objectMapper.readValue(decompressed, TypeWrapper.class);
            if (wrapper == null || wrapper.getClassName() == null) {
                return null;
            }
            return deserializeValue(wrapper);
        } catch (Exception e) {
            throw new SerializationException("Failed to deserialize value", e);
        }
    }

    private Object deserializeValue(TypeWrapper wrapper) throws Exception {
        if (wrapper.getData() == null) {
            return null;
        }

        JavaType type;
        if (wrapper.isCollection() && wrapper.getElementClassName() != null) {
            Class<?> collectionClass = Class.forName(wrapper.getClassName());
            Class<?> elementClass = Class.forName(wrapper.getElementClassName());
            type = typeFactory.constructCollectionType(
                    (Class<? extends Collection>) collectionClass,
                    elementClass
            );
        } else {
            type = typeFactory.constructType(Class.forName(wrapper.getClassName()));
        }
        return objectMapper.readValue(wrapper.getData(), type);
    }

    private byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipStream = new GZIPOutputStream(outputStream)) {
            gzipStream.write(data);
        }
        return outputStream.toByteArray();
    }

    private byte[] decompress(byte[] compressed) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(compressed);
        try (GZIPInputStream gzipStream = new GZIPInputStream(inputStream)) {
            return gzipStream.readAllBytes();
        }
    }

    private static class TypeWrapper {
        private String className;
        private String elementClassName;
        private String data;
        private boolean isCollection;

        public TypeWrapper() {
            // For Jackson deserialization
        }

        public TypeWrapper(Object value, ObjectMapper mapper) throws IOException {
            if (value == null) {
                return;
            }

            this.className = value.getClass().getName();
            this.isCollection = value instanceof Collection;
            
            if (isCollection && !((Collection<?>) value).isEmpty()) {
                Object firstElement = ((Collection<?>) value).iterator().next();
                if (firstElement != null) {
                    this.elementClassName = firstElement.getClass().getName();
                }
            }
            
            this.data = mapper.writeValueAsString(value);
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getElementClassName() {
            return elementClassName;
        }

        public void setElementClassName(String elementClassName) {
            this.elementClassName = elementClassName;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public boolean isCollection() {
            return isCollection;
        }

        public void setCollection(boolean collection) {
            isCollection = collection;
        }
    }
} 