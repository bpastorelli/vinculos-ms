package br.com.vinculos.validators;

import java.util.List;

import br.com.vinculos.errorheadling.RegistroException;

public interface Validators<T, X, V> {
	
	void validarPost(T dto) throws RegistroException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException;
	
	void validarPost(List<T> listDto) throws RegistroException;
	
	void validarPut(X dto, Long id) throws RegistroException;
	
	void validarPut(List<X> listDto) throws RegistroException;
	
	V validarGet(T dto) throws RegistroException;
}
