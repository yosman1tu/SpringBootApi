package com.coder.UpDownload;

import javax.annotation.Resource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
 
import com.coder.UpDownload.storage.StorageService;
 
@SpringBootApplication
public class Application implements CommandLineRunner{
 
	@Resource
	StorageService storageService;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
 
	@Override
	public void run(String... args) throws Exception {
		storageService.deleteAll();
		storageService.init();
	}
}