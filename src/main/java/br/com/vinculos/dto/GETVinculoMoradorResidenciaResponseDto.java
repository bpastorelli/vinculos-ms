package br.com.vinculos.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GETVinculoMoradorResidenciaResponseDto {

	private GETMoradorResponseDto morador;
	
}
