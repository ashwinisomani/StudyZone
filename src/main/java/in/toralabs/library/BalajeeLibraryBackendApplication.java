package in.toralabs.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories("in.toralabs.library.jpa.repository")
@EntityScan(basePackages = "in.toralabs.library")
@ComponentScan(basePackages = "in.toralabs.*")
@EnableAsync
@EnableScheduling
public class BalajeeLibraryBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BalajeeLibraryBackendApplication.class, args);
	}
}