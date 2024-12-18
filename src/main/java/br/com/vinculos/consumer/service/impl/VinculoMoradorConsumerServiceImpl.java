package br.com.vinculos.consumer.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.vinculos.consumer.service.ConsumerService;
import br.com.vinculos.dto.GETMoradoresSemResidenciaResponseDto;
import br.com.vinculos.dto.MoradorRequestDto;
import br.com.vinculos.dto.QueryResidenciaResponseDto;
import br.com.vinculos.dto.ResidenciaRequestDto;
import br.com.vinculos.dto.VinculoRequestDto;
import br.com.vinculos.entities.Morador;
import br.com.vinculos.entities.Residencia;
import br.com.vinculos.entities.VinculoResidencia;
import br.com.vinculos.repositories.VinculoResidenciaRepository;
import br.com.vinculos.senders.MoradorSender;
import br.com.vinculos.senders.ResidenciaSender;
import br.com.vinculos.utils.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VinculoMoradorConsumerServiceImpl implements ConsumerService<VinculoRequestDto> {
	
	@Autowired
	private MoradorSender moradorSender;
	
	@Autowired
	private ResidenciaSender residenciaSender;
	
	@Autowired
	private VinculoResidenciaRepository vinculoResidenciaRepository;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void processar(VinculoRequestDto processoDto) throws Exception {

		log.info("Registrando vinculo do morador com a residencia");
		
		Boolean retorno = this.processarComRetry(processoDto);
	
		log.info("Retorno do envio para processamento de vinculo: {}", retorno);
		
	}
	
	private VinculoResidencia convertToVinculoResidencia(GETMoradoresSemResidenciaResponseDto moradorResponse, QueryResidenciaResponseDto residenciaResponse) {
		
		VinculoResidencia vinculo = new VinculoResidencia();
		
		Morador morador = new Morador();
		morador.setId(moradorResponse.getMoradores().get(0).getId());
		
		Residencia residencia = new Residencia();
		residencia.setId(residenciaResponse.getResidencias().get(0).getId());
		
		vinculo.setMorador(morador);
		vinculo.setResidencia(residencia);
		vinculo.setGuide(UUID.randomUUID().toString());
		
		return vinculo;
		
	}
	
	private Boolean processarComRetry(VinculoRequestDto processoDto) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException, InterruptedException {
		
		int count = 0;
		int times = 20;
		
		QueryResidenciaResponseDto residenciaResponse = null;
		GETMoradoresSemResidenciaResponseDto moradorResponse = null;
		Boolean retorno = Boolean.FALSE;
		
		do {
			
			count ++;
			log.info("Tentativa {} para registrar vinculo de residencia.", count);
			
			MoradorRequestDto requestMorador = null;
			if (processoDto.getTicketMorador() != null) {
				requestMorador = MoradorRequestDto.builder()
					.guide(processoDto.getTicketMorador())
					.build();				
			} else {
				requestMorador = MoradorRequestDto.builder()
					.cpf(Utils.tratarCPF(processoDto.getCpfMorador()))
					.build();
			}
			
			moradorResponse = moradorSender.buscarPorFiltros(requestMorador);
			
			ResidenciaRequestDto requestResidencia = null;
			if (processoDto.getResidenciaId() != null) {
				requestResidencia = ResidenciaRequestDto.builder()
						.id(processoDto.getResidenciaId())
						.build();
			}else {
				requestResidencia = ResidenciaRequestDto.builder()
						.cep(processoDto.getCepResidencia())
						.numero(processoDto.getNumeroResidencia())
						.complemento(processoDto.getComplementoResidencia())
						.build();
			}
			
			residenciaResponse = residenciaSender.buscarResidenciasPorFiltro(requestResidencia);
			
			if((moradorResponse.getMoradores().size() > 0 && residenciaResponse.getResidencias().size() > 0) || count > times) {
				retorno = Boolean.TRUE;
			}
			
		} while (retorno.equals(Boolean.FALSE));
		
		
		if(moradorResponse.getMoradores().size() > 0 && residenciaResponse.residencias.size() > 0) {
			this.vinculoResidenciaRepository.save(this.convertToVinculoResidencia(moradorResponse, residenciaResponse));
			return Boolean.TRUE;
		} else
			return Boolean.FALSE;
		
	}
	
}
