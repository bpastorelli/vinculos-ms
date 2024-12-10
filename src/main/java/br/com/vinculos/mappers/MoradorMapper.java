package br.com.vinculos.mappers;

import org.mapstruct.Mapper;

import br.com.vinculos.dto.ProcessoCadastroDto;
import br.com.vinculos.entities.VinculoResidencia;

@Mapper(componentModel = "spring")
public abstract class MoradorMapper {
	
	public abstract VinculoResidencia processoCadastroDtoToVinculoResidencia(ProcessoCadastroDto dto);

}
