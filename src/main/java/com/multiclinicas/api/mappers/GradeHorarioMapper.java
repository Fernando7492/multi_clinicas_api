package com.multiclinicas.api.mappers;

import org.springframework.stereotype.Component;

import com.multiclinicas.api.dtos.GradeHorarioCreateDTO;
import com.multiclinicas.api.dtos.GradeHorarioDTO;
import com.multiclinicas.api.models.GradeHorario;

@Component
public class GradeHorarioMapper {

    public GradeHorarioDTO toDTO(GradeHorario grade) {
        if (grade == null) {
            return null;
        }
        return new GradeHorarioDTO(
            grade.getId(),
            grade.getMedico().getId(),
            grade.getDiaSemana(),
            grade.getHoraInicio(),
            grade.getHoraFim()
        );
    }

    public GradeHorario toEntity(GradeHorarioCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        GradeHorario grade = new GradeHorario();
        grade.setDiaSemana(dto.diaSemana());
        grade.setHoraInicio(dto.horaInicio());
        grade.setHoraFim(dto.horaFim());
        
        return grade;
    }
}