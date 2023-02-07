package hr.fer.ruazosa.audionotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AudioNotesBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AudioNotesBackendApplication.class, args);
	}

}
