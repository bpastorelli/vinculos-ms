package br.com.vinculos.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.vinculos.dto.VinculoResidenciaRequestDto;
import br.com.vinculos.entities.VinculoResidencia;

@Repository
public interface VinculoResidenciaRepository extends JpaRepository<VinculoResidencia, Long>  {
	
	@Transactional(readOnly = true)
	Optional<VinculoResidencia> findById(Long id);
	
	Optional<VinculoResidencia> findByGuide(String guide);
	
	List<VinculoResidencia> findByResidenciaIdAndMoradorId(Long residenciaId, Long moradorId);
	
	VinculoResidencia findByResidenciaIdOrMoradorId(Long residenciaId, Long moradorId);
	
	List<VinculoResidencia> findByMoradorId(Long moradorId);
	
	List<VinculoResidencia> findByResidenciaId(Long residenciaId);
	
	@Query(value = "select *"
			+ " from vinculo_residencia v "
			+ " where (v.morador_id = :#{#filter.moradorId} OR :#{#filter.moradorId} IS NULL) "
			+ " and (v.residencia_id = :#{#filter.residenciaId} OR :#{#filter.residenciaId} IS NULL) "
			, nativeQuery = true)
	public List<VinculoResidencia> findVinculoBy(@Param("filter") VinculoResidenciaRequestDto filter);

}
