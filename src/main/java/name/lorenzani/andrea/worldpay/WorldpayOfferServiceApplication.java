package name.lorenzani.andrea.worldpay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;

@SpringBootApplication
public class WorldpayOfferServiceApplication {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    /** The memorable words are stored hashed: whoever reads the database cannot delete the offers. */
    @Bean
    PasswordEncoder memorableWordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        SpringApplication.run(WorldpayOfferServiceApplication.class, args);
    }
}
