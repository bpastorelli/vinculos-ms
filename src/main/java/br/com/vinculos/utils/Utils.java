package br.com.vinculos.utils;

public abstract class Utils {
	
	public static String tratarCPF(String cpf) {
		
		return cpf.replace(".", "").replace("-", "");
		
	}

}
