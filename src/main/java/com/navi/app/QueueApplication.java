package com.navi.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class QueueApplication {

	public static void main(String[] args) {
		SpringApplication.run(QueueApplication.class, args);
	}

}
