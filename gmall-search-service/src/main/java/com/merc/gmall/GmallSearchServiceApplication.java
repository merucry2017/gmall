package com.merc.gmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = "com.merc.gmall")
public class GmallSearchServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallSearchServiceApplication.class, args);
	}

}
