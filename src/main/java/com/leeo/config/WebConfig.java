package com.leeo.config;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.leeo.common.web.bind.method.annotation.FormModelMethodArgumentResolver;
import com.leeo.common.web.bind.method.annotation.PageableMethodArgumentResolver;
import com.leeo.common.web.bind.method.annotation.SearchableMethodArgumentResolver;

@Configuration
//@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

	@Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new PageableMethodArgumentResolver());
        argumentResolvers.add(new SearchableMethodArgumentResolver());
//        argumentResolvers.add(new FormModelMethodArgumentResolver());
    }
	
//	@Override
//    public void addFormatters(FormatterRegistry registry) {
//        registry.addFormatterForFieldAnnotation(new TimestampFormatAnnotationFormatterFactory());
//        super.addFormatters(registry);
//    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
                .indentOutput(true)
                .dateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                .modulesToInstall(new ParameterNamesModule());
        converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
//        converters.add(new MappingJackson2XmlHttpMessageConverter(builder.xml().build()));
    }

	@Bean
	public ObjectMapper jsonMapper(){
	    ObjectMapper objectMapper = new ObjectMapper();
	    //null输出空字符串
	    objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
	        @Override
	        public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
	            jgen.writeString("");
	        }
	    });
	    return objectMapper;
	}
}