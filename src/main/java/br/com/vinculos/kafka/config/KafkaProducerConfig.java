package br.com.vinculos.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class KafkaProducerConfig {

	@Value(value = "${spring.kafka.bootstrap-servers}")
	private String bootStrapAddress;
	
	@Value(value = "${vinculo.topic.name}")
	private String topicVinculo;
	
	@Bean
	public NewTopic createVinculoTopic() {
		
		return new NewTopic(topicVinculo, 1,(short) 1);
	}
	
}
