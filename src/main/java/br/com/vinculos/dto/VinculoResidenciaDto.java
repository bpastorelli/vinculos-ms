package br.com.vinculos.dto;

import javax.persistence.Transient;

import br.com.vinculos.entities.Morador;
import br.com.vinculos.entities.Residencia;
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
public class VinculoResidenciaDto {
	
	private Morador morador;
	
	private Residencia residencia;
	
	@Transient
	private String guide;

}
