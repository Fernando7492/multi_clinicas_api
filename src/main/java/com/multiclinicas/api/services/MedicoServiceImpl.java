package com.multiclinicas.api.services;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;

import com.multiclinicas.api.exceptions.ResourceNotFoundException;
import com.multiclinicas.api.models.Medico;
import com.multiclinicas.api.models.Clinica;
import com.multiclinicas.api.models.Especialidade;
import com.multiclinicas.api.repositories.ClinicaRepository;
import com.multiclinicas.api.repositories.EspecialidadeRepository;
import com.multiclinicas.api.repositories.MedicoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MedicoServiceImpl implements MedicoService {
	
	private final MedicoRepository medicoRepository;
	private final ClinicaRepository clinicaRepository;
    private final EspecialidadeRepository especialidadeRepository;
	
	@Override
	@Transactional(readOnly = true)
	public List<Medico> findAllByClinicId(Long clinicId) {
		return medicoRepository.findAllByClinicaId(clinicId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Medico> findAllActiveByClinicId(Long clinicId) {
		return medicoRepository.findAllByClinicaIdAndAtivoTrue(clinicId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Medico findByIdAndClinicId(Long id, Long clinicId) {
		return medicoRepository.findById(id)
				.filter(m -> m.getClinica().getId().equals(clinicId))
				.orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado para esta clínica."));
	}
	
	@Override
	@Transactional
	public Medico create(Long clinicId, Medico medico, Set<Long> especialidadesIds) {
		Clinica clinica = clinicaRepository.findById(clinicId)
				.orElseThrow(() -> new ResourceNotFoundException("Não foi possível encontrar a clínica."));
		medico.setClinica(clinica);
		
		Set<Especialidade> especialidades = getEspecialidadesByIds(especialidadesIds, clinicId);
		medico.setEspecialidades(especialidades);
		
		return medicoRepository.save(medico);
		
	}
	
	@Override
	@Transactional
	public Medico update(Long id, Long clinicId, Medico medicoAtualizado, Set<Long> especialidadeIds) {
		Medico medicoExistente = findByIdAndClinicId(id, clinicId);
		
		medicoExistente.setNome(medicoAtualizado.getNome());
        medicoExistente.setTelefone(medicoAtualizado.getTelefone());
        medicoExistente.setTelefoneSecundario(medicoAtualizado.getTelefoneSecundario());
        medicoExistente.setAtivo(medicoAtualizado.getAtivo());
        medicoExistente.setDuracaoConsulta(medicoAtualizado.getDuracaoConsulta());
        
        Set<Especialidade> novasEspecialidades = getEspecialidadesByIds(especialidadeIds, clinicId);
        medicoExistente.getEspecialidades().clear();
        medicoExistente.getEspecialidades().addAll(novasEspecialidades);
        
        return medicoRepository.save(medicoExistente);
	}
	
	@Override
	@Transactional
	public void delete(Long id, Long clinicId) {
		Medico medicoExistente = findByIdAndClinicId(id, clinicId);
		medicoRepository.delete(medicoExistente);
	}

	private Set<Especialidade> getEspecialidadesByIds(Set<Long> ids, Long clinicId) {
    if (ids == null || ids.isEmpty()) {
        return Set.of();
    }
    return especialidadeRepository.findAllById(ids).stream()
        .filter(e -> e.getClinica().getId().equals(clinicId))
        .collect(Collectors.toSet());
}

}