package br.com.vinculos.errorheadling;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErroApiRegistro {

	private List<ErroRegistro> erros;
	
}
