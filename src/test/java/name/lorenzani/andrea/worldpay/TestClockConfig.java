package name.lorenzani.andrea.worldpay;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

/** Makes the tests independent of the time at which they run. */
@TestConfiguration
public class TestClockConfig {

    public static final Instant NOW = Instant.parse("2026-01-01T12:00:00Z");

    @Bean
    @Primary
    Clock fixedClock() {
        return Clock.fixed(NOW, ZoneOffset.UTC);
    }
}
