package com.config;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.undertow.UndertowBuilderCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
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

@Configuration
@EnableSwagger2
public class AppConfig_Swagger {
	@Value(value="${swagger.enabled}") //通过@Value 获取配置信息 
	Boolean SwaggerEnabled;
	
	@Bean
	public Docket createRestApi(){
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
				// 是否开启
				.enable(SwaggerEnabled).select()
				// 扫描的路径包，只要这些包中的类配置有swagger注解，则启用这些注解
				.apis(RequestHandlerSelectors.basePackage("com"))
				// 指定路径处理PathSelectors.any()代表所有的路径
				.paths(PathSelectors.any()).build().pathMapping("/");
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("小辰共享单车")
				.description("springboot | swagger")
				// 作者信息
				.contact(new Contact("柏家辉", "http://www.bay233.xyz", "baijiahui233@qq.com"))
				.version("1.0.0")
				.build();
	}

	// 工厂模式， 构造器模式
	@Bean
	public UndertowServletWebServerFactory undertowServletWebServerFactory() {
		UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
		factory.addBuilderCustomizers(new UndertowBuilderCustomizer() {
			@Override
			public void customize(Undertow.Builder builder) {
				builder.setServerOption(UndertowOptions.RECORD_REQUEST_START_TIME, true);
			}
		});
		return factory;
	}
}
