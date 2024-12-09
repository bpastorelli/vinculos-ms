package br.com.vinculos.dto;

import java.io.Serializable;
import java.util.List;

import br.com.vinculos.enums.PerfilEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GETMoradorSemResidenciasResponseDto implements Serializable {

	private static final long serialVersionUID = -5754246207015712518L;
	
	private Long id;
	
	private String nome;
	
	private String email;
	
	private String cpf;
	
	private String rg;
	
	private String telefone;
	
	private String celular;
	
	private PerfilEnum perfil;
	
	private Long associado;
	
	private Long posicao;
	
	private String guide;
	
	private List<GETResidenciaResponseDto> residencias;

}
