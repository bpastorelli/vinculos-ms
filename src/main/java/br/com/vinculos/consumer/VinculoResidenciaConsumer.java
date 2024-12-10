package br.com.vinculos.consumer;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import br.com.vinculos.consumer.service.ConsumerService;
import br.com.vinculos.dto.ProcessoCadastroDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class VinculoResidenciaConsumer {
	
	@Autowired
	private ConsumerService<ProcessoCadastroDto> consumerService;
	
	@KafkaListener(topics = "${vinculo.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
	public void consumer(@Payload ProcessoCadastroDto message) {
		
		log.info("Recebida a mensagem, enviando para o serviço...");
		
		try {
			this.consumerService.processar(message);
		} catch (Exception ex) {
			throw new AmqpRejectAndDontRequeueException(ex);
		};
		
	}

}
