package br.com.vinculos.mappers;

import org.mapstruct.Mapper;

import br.com.vinculos.dto.QueryResidenciaResponseDto;
import br.com.vinculos.dto.ResidenciaDto;

@Mapper(componentModel = "spring")
public abstract class ResidenciaMapper {
	
	public abstract QueryResidenciaResponseDto residenciaDtoToQueryResidenciaResponseDto(ResidenciaDto dto);

}
