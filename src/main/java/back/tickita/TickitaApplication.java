package back.tickita;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TickitaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TickitaApplication.class, args);
	}

}
