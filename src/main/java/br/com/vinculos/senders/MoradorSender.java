package br.com.vinculos.senders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.vinculos.dto.GETMoradoresSemResidenciaResponseDto;
import br.com.vinculos.dto.MoradorRequestDto;
import br.com.vinculos.utils.RestTemplateUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MoradorSender {
	
	@Value("${morador-ms.url}")
	public String URL;
	
	@Autowired
	private RestTemplate restTemplate;
	
	public GETMoradoresSemResidenciaResponseDto buscarMoradores(MoradorRequestDto request) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException{
		
		log.info("Consultando moradores no endpoint: {}", URL);
		
		RestTemplateUtil rest = RestTemplateUtil.builder()
				.URL(URL)
				.mediaType(MediaType.APPLICATION_JSON)
				.method(HttpMethod.GET)
				.restTemplate(restTemplate)
				.params(request)
				.build();
		
		return (GETMoradoresSemResidenciaResponseDto) rest.execute(GETMoradoresSemResidenciaResponseDto.class);
		
	}

}
