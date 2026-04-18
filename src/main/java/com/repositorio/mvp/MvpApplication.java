package com.repositorio.mvp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class MvpApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
			.ignoreIfMissing()
			.load();
		dotenv.entries()
			.forEach(entry -> System.setProperty(entry.getKey(), 
				entry.getValue()
			)
		);
		SpringApplication.run(MvpApplication.class, args);
	}
}
