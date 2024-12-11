package br.com.vinculos.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.vinculos.dto.GETVinculoMoradorResidenciaResponseDto;
import br.com.vinculos.dto.ResponsePublisherDto;
import br.com.vinculos.dto.VinculoResidenciaRequestDto;
import br.com.vinculos.errorheadling.RegistroException;
import br.com.vinculos.errorheadling.RegistroExceptionHandler;
import br.com.vinculos.response.Response;
import br.com.vinculos.services.VinculosService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(tags = "Cadastro de Vinculo - Morador a Residência")
@RequestMapping("/sgc/vinculo")
@CrossOrigin(origins = "*")
class VinculoResidenciaController extends RegistroExceptionHandler {
	
	@Autowired
	private VinculosService vinculosService;
	
	@ApiOperation(value = "Produz uma nova mensagem no Kafka para vincular um morador existente a uma residência existente.")
	@PostMapping(value = "/morador-residencia/novo")
	public ResponseEntity<?> cadastrarNovoAMQP(@Valid @RequestBody VinculoResidenciaRequestDto vinculoRequestBody,
											   BindingResult result ) throws RegistroException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException{
		
		log.info("Enviando mensagem para o consumer...");
		
		ResponsePublisherDto response = vinculosService.salvar(vinculoRequestBody);
		
		return response.getTicket() == null ? 
				ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response.getErrors()) : 
				ResponseEntity.status(HttpStatus.ACCEPTED).body(response.getTicket());
		
	}
	
	@ApiOperation(value = "Consulta um vinculo de residencia com morador informados.")
	@GetMapping(value = "/morador-residencia/consulta")
	public ResponseEntity<?> consultarVinculo(VinculoResidenciaRequestDto vinculoRequestBody) throws RegistroException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException{
		
		log.info("Buscando vinculos de residencia x morador");
		
		Response<?> response = null;
		if (vinculoRequestBody.getMoradorId() != null && vinculoRequestBody.getResidenciaId() == null)
			response = vinculosService.buscarPorMorador(vinculoRequestBody);
		else if (vinculoRequestBody.getMoradorId() == null && vinculoRequestBody.getResidenciaId() != null)
			response = vinculosService.buscarPorResidencia(vinculoRequestBody);
		else
			response = vinculosService.buscarPorMorador(vinculoRequestBody);
		
		return new ResponseEntity<>(response.getData(), HttpStatus.OK);
		
	}
	
	@ApiOperation(value = "Remove um vinculo de residencia com morador.")
	@DeleteMapping(value = "/morador-residencia/deletar")
	public ResponseEntity<?> removeVinculo(VinculoResidenciaRequestDto vinculoRequestBody) throws RegistroException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException{
		
		log.info("Removendo vinculo de residencia x morador");
		
		Response<GETVinculoMoradorResidenciaResponseDto> response = vinculosService.buscarPorMorador(vinculoRequestBody);
		
		return new ResponseEntity<>(response.getData(), HttpStatus.OK);
		
	}

}
