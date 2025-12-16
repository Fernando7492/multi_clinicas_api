package com.multiclinicas.api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.multiclinicas.api.models.GradeHorario;

@Repository
public interface GradeHorarioRepository extends JpaRepository<GradeHorario, Long> {

    List<GradeHorario> findAllByMedico_ClinicaId(Long clinicId);

    List<GradeHorario> findAllByMedicoIdAndMedico_ClinicaId(Long medicoId, Long clinicId);

    Optional<GradeHorario> findByIdAndMedico_ClinicaId(Long id, Long clinicId);
}