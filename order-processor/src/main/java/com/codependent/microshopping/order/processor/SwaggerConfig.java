package com.codependent.microshopping.order.processor;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Lists;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	private static final String API_KEY_NAME = "api_key";
    
    @Value("${api.key:dummyAPIKey}")
    private String apiKey;
	
	/**
     * bean de configuración principal de Swagger
     * @return objeto docket
     */
    @Bean
    public Docket ordersV1Api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.regex("/api/v1/.*"))                
            .build()
            .apiInfo(new ApiInfo("Foo Rest API","Servicios REST Foo","v1","",new Contact("", "", ""),"",""))
            .pathMapping("/")
            .securitySchemes(Lists.newArrayList(apiKey()))
            .securityContexts(Lists.newArrayList(securityContext()))
            .useDefaultResponseMessages(false)
        	.groupName("ordersV1");
    }

	private SecurityContext securityContext() {
		 return SecurityContext.builder()
	                .securityReferences(defaultAuth())
	                .forPaths(PathSelectors.regex("/api/v1/.*"))
	                .build();
	}

	private ApiKey apiKey() {
		return new ApiKey(apiKey, API_KEY_NAME, "query");
	}
	
	private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0]=authorizationScope;
        return Lists.newArrayList(new SecurityReference(apiKey, authorizationScopes));
    }
}
