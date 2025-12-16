package com.multiclinicas.api.services;

import com.multiclinicas.api.exceptions.ResourceNotFoundException;
import com.multiclinicas.api.models.GradeHorario;
import com.multiclinicas.api.models.Medico;
import com.multiclinicas.api.repositories.GradeHorarioRepository;
import com.multiclinicas.api.repositories.MedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeHorarioServiceImpl implements GradeHorarioService {

    private final GradeHorarioRepository gradeHorarioRepository;
    private final MedicoRepository medicoRepository;

    private static final String GRADE_NOT_FOUND_MSG = "Grade de horário não encontrada com o ID: ";
    private static final String MEDICO_NOT_FOUND_MSG = "Médico não encontrado ou não pertence a esta clínica. ID: ";

    @Override
    @Transactional
    public GradeHorario create(Long clinicId, Long medicoId, GradeHorario grade) {
        Medico medico = medicoRepository.findByIdAndClinicaId(medicoId, clinicId);
        
        if (medico == null) {
            throw new ResourceNotFoundException(MEDICO_NOT_FOUND_MSG + medicoId);
        }
        
        grade.setMedico(medico);
        return gradeHorarioRepository.save(grade);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeHorario> findAllByClinicId(Long clinicId) {
        return gradeHorarioRepository.findAllByMedico_ClinicaId(clinicId);
    }

    @Override
    @Transactional(readOnly = true)
    public GradeHorario findByIdAndClinicId(Long id, Long clinicId) {
        return gradeHorarioRepository.findByIdAndMedico_ClinicaId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException(GRADE_NOT_FOUND_MSG + id));
    }

    @Override
    @Transactional
    public void delete(Long id, Long clinicId) {
        GradeHorario grade = findByIdAndClinicId(id, clinicId);
        gradeHorarioRepository.delete(grade);
    }
}