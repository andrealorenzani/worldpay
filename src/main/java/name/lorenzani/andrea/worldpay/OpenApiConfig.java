package name.lorenzani.andrea.worldpay;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OpenApiConfig {

    @Bean
    OpenAPI offersApi() {
        return new OpenAPI().info(new Info()
                .title("Worldpay offers")
                .description("This API lets you create, delete and query an offer (as per Worldpay test)")
                .version("2.0")
                .contact(new Contact().email("work@andrea.lorenzani.name"))
                .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
