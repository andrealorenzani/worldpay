package name.lorenzani.andrea.worldpay;

import io.github.robwin.swagger.test.SwaggerAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URISyntaxException;
import java.nio.file.Paths;

@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class WorldpayOfferControllerServiceApplicationTests {

	@LocalServerPort
	int serverPort;

	@Test
	public void contextLoads() {
	}

	@Test
	public void validateThatImplementationMatchesDocumentationSpecification() throws URISyntaxException {
		String designFirstSwagger = Paths.get(WorldpayOfferControllerServiceApplicationTests.class.
				getResource("/swagger/swagger.yml").toURI()).toString();
		SwaggerAssertions.assertThat("http://localhost:"+serverPort+"/v2/api-docs?group=Api")
				.isEqualTo(designFirstSwagger);
	}
}
