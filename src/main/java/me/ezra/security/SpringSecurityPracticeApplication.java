package me.ezra.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class SpringSecurityPracticeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityPracticeApplication.class, args);
	}

}
