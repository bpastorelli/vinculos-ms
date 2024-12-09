package br.com.vinculos.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryResidenciaResponseDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public List<GETResidenciaResponseDto> residencias;
	
}
