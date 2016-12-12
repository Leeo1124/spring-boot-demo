package com.leeo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 
 * 访问地址http://localhost:8080/swagger-ui.html
 *
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {
	
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.leeo.swagger"))
                .paths(PathSelectors.any())
//                .paths(PathSelectors.regex("/swagger.*"))//过滤的接口
                .build();
    }
    private ApiInfo apiInfo() {
    	Contact contact = new Contact("Leeo", "http://my.oschina.net/leeo", "aaa@qq.com");
        return new ApiInfoBuilder()
                .title("Spring Boot中使用Swagger2构建RESTful APIs")
                .description("api根地址：http://localhost:8080/")
                .termsOfServiceUrl("http://localhost:8080/")
                .contact(contact)
                .version("1.0")
                .build();
    }
}