package name.lorenzani.andrea.worldpay.controller;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import name.lorenzani.andrea.worldpay.WorldpayOfferServiceApplication;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WorldpayOfferServiceApplication.class)
@AutoConfigureWireMock(port = 0, stubs = "classpath:/stubs")
public abstract class ContractVerifierBaseTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    }

}