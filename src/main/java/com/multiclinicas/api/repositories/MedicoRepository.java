package com.multiclinicas.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.multiclinicas.api.models.Medico;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
	
	List<Medico> findAllByClinicaId(Long clinicId);
	
	List<Medico> findAllByClinicaIdAndAtivoTrue(Long clinicId);
	
	Medico findByIdAndClinicaId(Long id, Long clinicId);
	
	boolean existsByCrmAndClinicaId(String crm, Long clinicId);
	
}