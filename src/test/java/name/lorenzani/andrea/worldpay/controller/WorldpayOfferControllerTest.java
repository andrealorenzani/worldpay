package name.lorenzani.andrea.worldpay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import name.lorenzani.andrea.worldpay.exception.BadRequest;
import name.lorenzani.andrea.worldpay.exception.InternalServerError;
import name.lorenzani.andrea.worldpay.model.Offer;
import name.lorenzani.andrea.worldpay.model.Offers;
import name.lorenzani.andrea.worldpay.model.StoredOffer;
import name.lorenzani.andrea.worldpay.service.OfferService;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.Same;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class WorldpayOfferControllerTest {

    @LocalServerPort
    int serverPort;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OfferService offerService;

    private Offer basicOkOffer;

    @Before
    public void setUp() {
        basicOkOffer = new Offer();
        basicOkOffer.setDescription("Description");
        basicOkOffer.setTitle("Title");
        basicOkOffer.setVendor("Vendor");
        basicOkOffer.setMemorableWord("Memorable");
        basicOkOffer.setPrice(new BigDecimal(1000));
        RestAssured.port = serverPort;
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void createOffer_shouldGetOkResponse() throws Exception {

        String title = "This is a mocked store";

        StoredOffer mockStore = new StoredOffer();
        mockStore.setTitle(title);

        when(offerService.store(any(Offer.class))).thenReturn(mockStore);

        //When
        given()
            .body(objectMapper.writeValueAsBytes(basicOkOffer))
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .log().everything().
        when()
            .post("/offer").
        then().log().everything().
            statusCode(HttpStatus.OK.value())
            .body("title", is(equalTo(title)));
    }

    @Test
    public void createOffer_shouldGetNokResponse() throws Exception {
        //Given
        when(offerService.store(any(Offer.class)))
                .thenThrow(new BadRequest("This is a mocked error"));
        //When
        given()
            .body(objectMapper.writeValueAsBytes(basicOkOffer))
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .log().everything().
        when()
            .post("/offer").
        then().log().everything().
            statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void createOffer_shouldHandleInternalServerError() throws Exception {
        //Given
        when(offerService.store(any(Offer.class)))
                .thenThrow(new InternalServerError("This is a mocked error"));
        //When
        given()
            .body(objectMapper.writeValueAsBytes(basicOkOffer))
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .log().everything().
            when()
            .post("/offer").
            then().log().everything().
            statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    public void retrieveOffer_shouldGetOkResponse() throws Exception {

        String title = "Mock inRes";
        Offers res = new Offers();
        StoredOffer inRes = new StoredOffer();
        inRes.setTitle(title);
        res.setOffers(Collections.singletonList(inRes));

        //Given
        when(offerService.retrieve(isNull(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(res);
        //When
        List<StoredOffer> retrieved = given()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .log().everything().
            when()
            .get("/offer").
            then().log().everything().
            statusCode(HttpStatus.OK.value())
            .extract()
            .body().jsonPath().getList("offers", StoredOffer.class);
        Assert.assertThat(retrieved, is(equalTo(res.getOffers())));
    }

    @Test
    public void retrieveOffer_shouldGetNokResponse() throws Exception {

       //Given
        when(offerService.retrieve(isNull(), isNull(), isNull(), isNull(), isNull()))
                .thenThrow(new BadRequest("Mock error"));
        //When
        given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .log().everything().
                        when()
                .get("/offer").
                        then().log().everything().
                        statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void retrieveOffer_shouldGetInternalServerError() throws Exception {

       //Given
        when(offerService.retrieve(isNull(), isNull(), isNull(), isNull(), isNull()))
                .thenThrow(new InternalServerError("Mock error"));
        //When
        given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .log().everything().
                        when()
                .get("/offer").
                        then().log().everything().
                        statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    public void deleteOffer_shouldGetOkResponse() throws Exception {

        StoredOffer so = new StoredOffer();
        so.setTitle("title");

        //Given
        when(offerService.delete(anyString(), anyString()))
                .thenReturn(so);
        //When
        StoredOffer retrieved = given()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .log().everything().
            when()
            .delete("/offer/a/b").
            then().log().everything().
            statusCode(HttpStatus.OK.value())
            .extract()
            .body().jsonPath().getObject(".", StoredOffer.class);
        Assert.assertThat(retrieved, is(equalTo(so)));
    }

    @Test
    public void deleteOffer_shouldGetNokResponse() throws Exception {

       //Given
        when(offerService.delete(anyString(), anyString()))
                .thenThrow(new BadRequest("Mock error"));
        //When
        given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .log().everything().
                        when()
                .delete("/offer/a/b").
                        then().log().everything().
                        statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void deleteOffer_shouldGetInternalServerError() throws Exception {

       //Given
        when(offerService.delete(anyString(), anyString()))
                .thenThrow(new InternalServerError("Mock error"));
        //When
        given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .log().everything().
                        when()
                .delete("/offer/a/b").
                        then().log().everything().
                        statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

}