package br.com.vinculos;

import java.time.Duration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class VinculosMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(VinculosMsApplication.class, args);
	}
	
	@Bean
	public RestTemplate getRestTemplate() {
	    return new RestTemplate();
	}
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
	 
	  return builder
	    .setConnectTimeout(Duration.ofMillis(3000))
	    .setReadTimeout(Duration.ofMillis(3000))
	    .build();
	}
}
