package br.com.vinculos.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.vinculos.dto.AtualizaVinculoResidenciaDto;
import br.com.vinculos.dto.CabecalhoResponsePublisherDto;
import br.com.vinculos.dto.GETMoradoresSemResidenciaResponseDto;
import br.com.vinculos.dto.GETVinculoMoradorResidenciaResponseDto;
import br.com.vinculos.dto.GETVinculoResidenciaMoradorResponseDto;
import br.com.vinculos.dto.MoradorRequestDto;
import br.com.vinculos.dto.QueryResidenciaResponseDto;
import br.com.vinculos.dto.ResidenciaRequestDto;
import br.com.vinculos.dto.ResponsePublisherDto;
import br.com.vinculos.dto.VinculoResidenciaRequestDto;
import br.com.vinculos.entities.VinculoResidencia;
import br.com.vinculos.errorheadling.RegistroException;
import br.com.vinculos.repositories.VinculoResidenciaRepository;
import br.com.vinculos.response.Response;
import br.com.vinculos.senders.MoradorSender;
import br.com.vinculos.senders.ResidenciaSender;
import br.com.vinculos.validators.Validators;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VinculosService {
	
	@Value("${guide.limit}")
	private int guideLimit;
	
	@Autowired
	private MoradorSender moradorSender;
	
	@Autowired
	private ResidenciaSender residenciaSender;
	
	@Autowired
	private VinculoResidenciaRepository vinculoResidenciaRepository;
	
	@Autowired
	private Validators<VinculoResidenciaRequestDto, AtualizaVinculoResidenciaDto, List<VinculoResidencia>> validator;
	
	public ResponsePublisherDto salvar(VinculoResidenciaRequestDto vinculoRequestBody) throws RegistroException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
		
		log.info("Cadastrando um vinculo de morador com residencia: {}", vinculoRequestBody.toString());
		
		vinculoRequestBody.setGuide(this.gerarGuide());
		
		this.validator.validarPost(vinculoRequestBody);
		
		//Envia para a fila de Vinculo de Residencia
		log.info("Enviando mensagem " +  vinculoRequestBody.toString() + " para o consumer.");
		
		//this.producer.producerAsync(vinculoRequestBody);
		
		ResponsePublisherDto response = ResponsePublisherDto
				.builder()
				.ticket(CabecalhoResponsePublisherDto
						.builder()
						.ticket(vinculoRequestBody.getGuide())
						.build())
				.build();
		
		return response;
		
	}
	
	public Response<GETVinculoMoradorResidenciaResponseDto> buscarPorMorador(VinculoResidenciaRequestDto request) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException, RegistroException {
		
		List<VinculoResidencia> vinculos = this.validator.validarGet(request);
		
		MoradorRequestDto requestMorador = MoradorRequestDto.builder()
				.id(request.getMoradorId())
				.build();
		
		GETMoradoresSemResidenciaResponseDto moradorResponse = moradorSender.buscarPorFiltros(requestMorador);
		
		GETVinculoMoradorResidenciaResponseDto vinculo = null;
		
		if (moradorResponse.getMoradores().size() > 0) {
			vinculo = GETVinculoMoradorResidenciaResponseDto.builder()
				.morador(moradorResponse.getMoradores().get(0))
				.build();
		}
		
		if(vinculos.size() > 0) {
			List<String> ids = new ArrayList<>();
			ids = vinculos.stream().map(v -> v.getResidencia().getId().toString()).toList();
			ResidenciaRequestDto requestResidencia = ResidenciaRequestDto.builder()
					.ids(ids)
					.build();
			
			QueryResidenciaResponseDto residenciaResponse = residenciaSender.buscarResidencias(requestResidencia);
			
			vinculo = GETVinculoMoradorResidenciaResponseDto.builder()
					.morador(moradorResponse.moradores.get(0))
					.build();
			
			moradorResponse.getMoradores().get(0).setResidencias(residenciaResponse.getResidencias().size() > 0 ? residenciaResponse.getResidencias() : new ArrayList<>());
		}else {
			moradorResponse.getMoradores().get(0).setResidencias(new ArrayList<>());
		}
		
		Response<GETVinculoMoradorResidenciaResponseDto> response = new Response<GETVinculoMoradorResidenciaResponseDto>(); 
		
		response.setData(vinculo);
		
		return response;
		
	}
	
	public Response<GETVinculoResidenciaMoradorResponseDto> buscarPorResidencia(VinculoResidenciaRequestDto request) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException, RegistroException {
		
		List<VinculoResidencia> vinculos = this.validator.validarGet(request);
		
		List<String> ids = new ArrayList<>();
		ids.add(request.getResidenciaId().toString());
		ResidenciaRequestDto requestResidencia = ResidenciaRequestDto.builder()
				.ids(ids)
				.build();
		
		QueryResidenciaResponseDto residenciaResponse = residenciaSender.buscarResidencias(requestResidencia);
		
		GETVinculoResidenciaMoradorResponseDto vinculo = null;
		
		if (residenciaResponse.getResidencias().size() > 0) {
			vinculo = GETVinculoResidenciaMoradorResponseDto.builder()
				.residencia(residenciaResponse.residencias.get(0))
				.build();
		}
		
		if(vinculos.size() > 0) {
			ids = new ArrayList<>();
			ids = vinculos.stream().map(m -> m.getMorador().getId().toString()).collect(Collectors.toList());
			MoradorRequestDto requestMorador = MoradorRequestDto.builder()
					.ids(ids)
					.build();
			
			GETMoradoresSemResidenciaResponseDto moradorResponse = moradorSender.buscarPorIds(requestMorador);
			
			residenciaResponse.getResidencias().get(0).setMoradores(residenciaResponse.getResidencias().size() > 0 ? moradorResponse.getMoradores() : new ArrayList<>());
		}else {
			residenciaResponse.getResidencias().get(0).setMoradores(new ArrayList<>());
		}
		
		Response<GETVinculoResidenciaMoradorResponseDto> response = new Response<GETVinculoResidenciaMoradorResponseDto>(); 
		
		response.setData(vinculo);
		
		return response;
		
	}
	
	public Response<Boolean> existeRelacao(VinculoResidenciaRequestDto request) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException, RegistroException {
		
		List<VinculoResidencia> vinculos = this.validator.validarGet(request);
		
		Response<Boolean> response = new Response<Boolean>();
		
		if	(vinculos.size() > 0 && (request.getResidenciaId() != null && request.getMoradorId() != null))
			response.setData(Boolean.TRUE);
		else if (request.getResidenciaId() == null || request.getMoradorId() == null)
			response.setData(Boolean.FALSE);
		else 
			response.setData(Boolean.FALSE);
			
		return response;
		
	}
	
	public void deletarVinculo(VinculoResidenciaRequestDto request) {
		
		List<VinculoResidencia> vinculo = vinculoResidenciaRepository.findByResidenciaIdAndMoradorId(request.getResidenciaId(), request.getMoradorId());
		
		vinculoResidenciaRepository.delete(vinculo.get(0));
		
	}
	
	private String gerarGuide() {

		String guide = null;
		int i = 0;
		boolean ticketValido = false;
		
		do {
			i++;
			if(this.vinculoResidenciaRepository.findByGuide(guide).isPresent())
				guide = UUID.randomUUID().toString();
			else if(guide == null)
				guide = UUID.randomUUID().toString();
			else {
				ticketValido = true;
			}
			
		}while(!ticketValido && i < guideLimit);
		
		return guide;
	}

}
