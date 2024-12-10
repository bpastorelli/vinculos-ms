package br.com.vinculos.dto;

import java.io.Serializable;

import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProcessoCadastroDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonUnwrapped
	private MoradorDto morador;
	
	@Transient
	private String guide;

}
