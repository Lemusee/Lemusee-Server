package com.lemusee.lemusee_prj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class LemuseeApplication {

	public static void main(String[] args) {
		SpringApplication.run(LemuseeApplication.class, args);
	}
}
