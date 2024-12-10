package br.com.vinculos.consumer.service;

public interface ConsumerService<T> {

	void processar(T message) throws Exception;
	
}
