package vn.iotstar;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import vn.iotstar.Config.StorageProperties;
import vn.iotstar.Service.IStorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class) 
public class DemoSpringbootSt561Application {
	public static void main(String[] args) {
		SpringApplication.run(DemoSpringbootSt561Application.class, args);
	}

	@Bean
	CommandLineRunner init(IStorageService storageService) {
		return (args -> {
			storageService.init();
		});
	}

}
