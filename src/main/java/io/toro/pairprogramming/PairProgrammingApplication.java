package io.toro.pairprogramming;

import java.io.File;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PairProgrammingApplication {

	private final String ROOT_DIRECTORY = System.getProperty("user.dir") + File.separator + "storage";

	@PostConstruct
	public void createStorageDirectory() {
		//String absoluteFilePath = ROOT_DIRECTORY;
		File file = new File(ROOT_DIRECTORY);
		if (file.mkdir()) {
			System.out.println("File is created!");
		} else {
			System.out.println("File is already existed!");
		}
	}
	public static void main(String[] args) {
		SpringApplication.run(PairProgrammingApplication.class, args);
	}
}
