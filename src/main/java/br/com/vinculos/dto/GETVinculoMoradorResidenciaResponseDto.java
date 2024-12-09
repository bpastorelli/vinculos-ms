package br.com.vinculos.dto;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GETVinculoMoradorResidenciaResponseDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public GETMoradorResponseDto morador;
	
}
