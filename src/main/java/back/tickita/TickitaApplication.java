package back.tickita;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(exclude={SecurityAutoConfiguration.class})
@EnableJpaAuditing
@OpenAPIDefinition(servers = {@Server(url = "https://api.tickita.net")})
public class TickitaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TickitaApplication.class, args);
	}

}
