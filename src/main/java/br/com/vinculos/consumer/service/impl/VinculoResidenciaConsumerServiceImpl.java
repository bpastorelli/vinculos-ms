package br.com.vinculos.consumer.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.vinculos.consumer.service.ConsumerService;
import br.com.vinculos.dto.GETMoradoresSemResidenciaResponseDto;
import br.com.vinculos.dto.MoradorRequestDto;
import br.com.vinculos.dto.ProcessoCadastroDto;
import br.com.vinculos.dto.QueryResidenciaResponseDto;
import br.com.vinculos.dto.ResidenciaRequestDto;
import br.com.vinculos.entities.Morador;
import br.com.vinculos.entities.Residencia;
import br.com.vinculos.entities.VinculoResidencia;
import br.com.vinculos.repositories.VinculoResidenciaRepository;
import br.com.vinculos.senders.MoradorSender;
import br.com.vinculos.senders.ResidenciaSender;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VinculoResidenciaConsumerServiceImpl implements ConsumerService<ProcessoCadastroDto> {
	
	@Autowired
	private MoradorSender moradorSender;
	
	@Autowired
	private ResidenciaSender residenciaSender;
	
	@Autowired
	private VinculoResidenciaRepository vinculoResidenciaRepository;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void processar(ProcessoCadastroDto processoDto) throws Exception {

		log.info("Registrando vinculo do morador {}, com a residencia {}", processoDto.getMorador().getNome(), processoDto.getMorador().getResidencia().getEndereco() + ", " + processoDto.getMorador().getResidencia().getNumero());
		
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
	
	private Boolean processarComRetry(ProcessoCadastroDto processoDto) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException, InterruptedException {
		
		int count = 0;
		int times = 20;
		
		QueryResidenciaResponseDto residenciaResponse = null;
		GETMoradoresSemResidenciaResponseDto moradorResponse = null;
		Boolean retorno = Boolean.FALSE;
		
		do {
			
			Thread.sleep(1000);
			
			count ++;
			log.info("Tentativa {} para registrar vinculo de residencia para o morador {} com a residencia {}.", count, processoDto.getMorador().getNome(), processoDto.getMorador().getResidencia().getEndereco());
			
			MoradorRequestDto requestMorador = MoradorRequestDto.builder()
					.cpf(processoDto.getMorador().getCpf())
					.build();
			
			moradorResponse = moradorSender.buscarMoradores(requestMorador);
			
			ResidenciaRequestDto requestResidencia = ResidenciaRequestDto.builder()
					.cep(processoDto.getMorador().getResidencia().getCep())
					.numero(processoDto.getMorador().getResidencia().getNumero())
					.complemento(processoDto.getMorador().getResidencia().getComplemento())
					.build();
			
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
