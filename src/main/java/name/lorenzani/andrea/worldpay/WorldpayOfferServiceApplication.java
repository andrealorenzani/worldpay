package name.lorenzani.andrea.worldpay;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.ws.client.core.WebServiceTemplate;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Locale;

import static com.google.common.base.Predicates.not;

@EnableSwagger2
@SpringBootApplication
@EnableAsync
@ComponentScan(basePackages = {"name.lorenzani.andrea.worldpay"})
public class WorldpayOfferServiceApplication {

	@Bean
	public LocaleResolver localeResolver() {
		// This is needed because my locale is not english :P
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(Locale.ENGLISH);
		return slr;
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setDateFormat(new SimpleDateFormat("dd-MM-yyyy hh:mm:ss"));
		objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
		return objectMapper;
	}

	@Bean
	public WebServiceTemplate webServiceTemplate() {
		WebServiceTemplate template = new WebServiceTemplate();
		return template;
	}

	@Value("${swagger.api.version}")
	String swaggerApiVersion;

	@Bean
	public Docket swaggerApiDocumentation(){
		return new Docket(DocumentationType.SWAGGER_2)
				.useDefaultResponseMessages(false)
				.groupName("Api")
				.directModelSubstitute(LocalDate.class, String.class)
				.apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.basePackage("name.lorenzani.andrea.worldpay.controller"))
				.build()
				.pathMapping("/");
	}

	@Bean
	public Docket swaggerInternalDocumentation(){
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("Internal")
				.select()
				.apis(not(RequestHandlerSelectors.basePackage("name.lorenzani.andrea.worldpay")))
				.build()
				.pathMapping("/")
				.tags(new Tag("Status endpoints", "All endpoints relating to the status of the Microservice"));
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("Worldpay offer microservice")
				.description("API Documentation")
				.version(swaggerApiVersion)
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(WorldpayOfferServiceApplication.class, args);
	}
}
