package com.multiclinicas.api.services;

import java.util.List;

import com.multiclinicas.api.models.GradeHorario;

public interface GradeHorarioService {
    GradeHorario create(Long clinicId, Long medicoId, GradeHorario grade);
    List<GradeHorario> findAllByClinicId(Long clinicId);
    GradeHorario findByIdAndClinicId(Long id, Long clinicId);
    void delete(Long id, Long clinicId);
}