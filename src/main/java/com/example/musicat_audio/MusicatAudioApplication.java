package com.example.musicat_audio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example")
public class MusicatAudioApplication {

	public static void main(String[] args) {
		SpringApplication.run(MusicatAudioApplication.class, args);
	}

}
