package br.com.vinculos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VinculoResidenciaRequestDto {
	
	private Long residenciaId;
	
	private Long moradorId;
	
	private String guide;

}
