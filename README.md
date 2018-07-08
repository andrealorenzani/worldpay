# Worldpay Offer microservice

# Dependencies
This service depends on the following technologies:
 * Spring Boot (2.0.3.RELEASE) 
 * H2 database and JPA
 * SWAGGER contracts and SpringFox
 * Jackson JSR310
 * Lombok because I am lazy :P 

# How to run
## Run as a Spring Boot local application
`mvn clean verify spring-boot:run -Dspring.profiles.active=local,logging`

A profile is necesary since the service is not design to fall back to any default profile. `local` disables all integration tools like Eureka or Configservice making sure the project runs in isolation.
`logging` enables logging through logback.

## Management (actuators)
The following actuators are enabled to manage the application:
 * `http://localhost:9000/actuator/info` to get the info related the currently running version of the application
 * `http://localhost:9000/actuator/env` to get the environment/properties loaded by the microervice
 * `http://localhost:9000/actuator/beans` lists all the beans that are loaded

## Calling the API
Although curl is always the easiest way to call an API, I setup the swaggerUI webpage that is much easier (and gives also back the CURL for accessing the API, if needed).
The page can be found at `http://localhost:9000/swagger-ui.html`


# Release Notes

## v1.0.1
| Type | Description | Notes |
| -- | -- | -- |
| Feature | Offers can be deleted only when they are not expired | The text of the test says: `Offers may also be explicitly cancelled before they expire.` |
| BAU     | Cleaned pom (unused plugins) and code (unused Jax serializers) ||
| Test    | Tested that an expired offer cannot be retrieved | Tested through contract verifier and H2 data injected| 

## v1.0.0
| Type | Description | Notes |
| -- | -- | -- |
| Feature | Connection and usage of H2 DB | Inmemory H2 is easily setup on spring-boot |
| Test    | Created script for populating test database | The script is in test, under resource folder, `data.sql`|
| Deliverable | SwaggerUI | Available at `http://localhost:9000/swagger-ui.html` | 
| Feature | Definition of the swagger contract | swagger.yml is in the resource folder |
| Test    | Enrichment of the controller with annotations for assertj-swagger | This is used to generate the swagger definition based on the controller definition|
| Test    | Test against swagger contract | assertj-swagger setup in the class WorldpayOfferControllerServiceApplicationTests |
| Test    | Contract verify coverage for put, retrieve and delete | Contract test (integration) are under resource/contracts |