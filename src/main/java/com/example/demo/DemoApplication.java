package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

		final String say = "Hello world";
		var age = 24;

		if (3>1){
			System.out.println(say);
			System.out.println(age);
		}
	}
}
