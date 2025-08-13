package com.DPhong.storeMe.config;

import com.DPhong.storeMe.util.RoleBasedAnnotationIntrospector;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfiguration {
  @Bean
  public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
    ObjectMapper mapper = builder.createXmlMapper(false).build();
    mapper.setAnnotationIntrospector(
        AnnotationIntrospector.pair(
            new RoleBasedAnnotationIntrospector(),
            mapper.getSerializationConfig().getAnnotationIntrospector()));
    return mapper;
  }
}
