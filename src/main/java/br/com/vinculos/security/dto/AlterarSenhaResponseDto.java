package br.com.vinculos.security.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlterarSenhaResponseDto {
	
	private String senha;

}
