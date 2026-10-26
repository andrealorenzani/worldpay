package name.lorenzani.andrea.worldpay;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The published contract is docs/openapi.json: the API generated from the code must match it. When the API is
 * changed on purpose, regenerate the file with {@code OPENAPI_UPDATE=true ./mvnw test -Dtest=OpenApiContractTest}.
 */
@SpringBootTest
@AutoConfigureMockMvc
class OpenApiContractTest {

    private static final Path CONTRACT = Path.of("docs", "openapi.json");

    @Autowired
    private MockMvc mvc;

    @Test
    void theImplementationMatchesThePublishedContract() throws Exception {
        String generated = mvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        if ("true".equals(System.getenv("OPENAPI_UPDATE"))) {
            Files.writeString(CONTRACT, generated);
        }

        mvc.perform(get("/v3/api-docs"))
                .andExpect(content().json(Files.readString(CONTRACT), true));
    }

    @Test
    void swaggerUiIsAvailable() throws Exception {
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    }
}
