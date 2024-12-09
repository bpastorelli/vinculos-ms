package br.com.vinculos.utils;

public interface RestTemplateInterface<T, O> {
	
	public T restTemplate(O clazz);

}
