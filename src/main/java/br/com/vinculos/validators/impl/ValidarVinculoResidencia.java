package br.com.vinculos.validators.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.vinculos.dto.AtualizaVinculoResidenciaDto;
import br.com.vinculos.dto.MoradorRequestDto;
import br.com.vinculos.dto.ResidenciaRequestDto;
import br.com.vinculos.dto.VinculoResidenciaRequestDto;
import br.com.vinculos.entities.VinculoResidencia;
import br.com.vinculos.errorheadling.ErroRegistro;
import br.com.vinculos.errorheadling.RegistroException;
import br.com.vinculos.repositories.VinculoResidenciaRepository;
import br.com.vinculos.senders.MoradorSender;
import br.com.vinculos.senders.ResidenciaSender;
import br.com.vinculos.validators.Validators;

@Component
public class ValidarVinculoResidencia implements Validators<VinculoResidenciaRequestDto, AtualizaVinculoResidenciaDto, List<VinculoResidencia>>{
	
	@Autowired
	private VinculoResidenciaRepository vinculoRepository;
	
	@Autowired
	private MoradorSender moradorSender;
	
	@Autowired
	private ResidenciaSender residenciaSender;
	
	private static final String TITULO = "Vinculo de residencia";
	
	@Override
	public void validarPost(VinculoResidenciaRequestDto t) throws RegistroException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
		
		List<VinculoResidenciaRequestDto> residencias = new ArrayList<VinculoResidenciaRequestDto>();
		residencias.add(t);
		
		validar(residencias);
		
	}
	
	@Override
	public void validarPut(AtualizaVinculoResidenciaDto x, Long id) throws RegistroException {
		// TODO Auto-generated method stub
		
	}
	
	public void validar(List<VinculoResidenciaRequestDto> t) throws RegistroException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
		
		RegistroException errors = new RegistroException();

		t.forEach(r -> {
			
			if(r.getMoradorId() == 0L || r.getMoradorId() == null)
				errors.getErros().add(new ErroRegistro("", TITULO, " Campo morador é obrigatório!")); 
			else {
				MoradorRequestDto request = MoradorRequestDto.builder()
						.id(r.getMoradorId())
						.content(true)
						.build();
				
				try {
					if(moradorSender.buscarMoradores(request).getMoradores().size() == 0)
						errors.getErros().add(new ErroRegistro("", TITULO, " O morador selecionado não existe!"));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} 
			}
			
			if(r.getResidenciaId() == 0L || r.getResidenciaId() == null)
				errors.getErros().add(new ErroRegistro("", TITULO, " Campo residencia é obrigatório!")); 
			else {
				
				List<String> ids = new ArrayList<>();
				ids.add(r.getResidenciaId().toString());
				ResidenciaRequestDto request = ResidenciaRequestDto.builder()
						.ids(ids)
						.build();
				
				try {
					if(residenciaSender.buscarResidencias(request).getResidencias().size() == 0)
						errors.getErros().add(new ErroRegistro("", TITULO, " A residência selecionada não existe!"));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} 
			}
			
			if(this.vinculoRepository.findByResidenciaIdAndMoradorId(r.getResidenciaId(), r.getMoradorId()).size() > 0)
				errors.getErros().add(new ErroRegistro("", TITULO, " Este morador já está vinculado a essa residência!")); 
			
		});
		
		if(!errors.getErros().isEmpty())
			throw errors;
		
	}

	@Override
	public void validarPost(List<VinculoResidenciaRequestDto> listDto) throws RegistroException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validarPut(List<AtualizaVinculoResidenciaDto> listDto) throws RegistroException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<VinculoResidencia> validarGet(VinculoResidenciaRequestDto dto) throws RegistroException {
		
		RegistroException errors = new RegistroException();
		
		if(dto.getMoradorId() == null)
			errors.getErros().add(new ErroRegistro("", TITULO, " Campo moradorId é obrigatório!")); 
		
		List<VinculoResidencia> vinculos = vinculoRepository.findVinculoBy(dto);
		
		if (vinculos.size() == 0)
			errors.getErros().add(new ErroRegistro("", TITULO, " Não foi encontrado vinculo de residencias para o morador!"));
		
		if(!errors.getErros().isEmpty())
			throw errors;
		
		return vinculos;
		
	}

}
