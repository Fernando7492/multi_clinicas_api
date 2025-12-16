package com.multiclinicas.api.services;

import com.multiclinicas.api.exceptions.ResourceNotFoundException;
import com.multiclinicas.api.models.Clinica;
import com.multiclinicas.api.models.GradeHorario;
import com.multiclinicas.api.models.Medico;
import com.multiclinicas.api.repositories.GradeHorarioRepository;
import com.multiclinicas.api.repositories.MedicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeHorarioServiceTest {

    @Mock
    private GradeHorarioRepository gradeHorarioRepository;

    @Mock
    private MedicoRepository medicoRepository;

    @InjectMocks
    private GradeHorarioServiceImpl gradeHorarioService;

    private GradeHorario gradeHorario;
    private Medico medico;
    private final Long clinicId = 1L;
    private final Long medicoId = 10L;
    private final Long gradeId = 100L;

    @BeforeEach
    void setUp() {
        Clinica clinica = new Clinica();
        clinica.setId(clinicId);

        medico = new Medico();
        medico.setId(medicoId);
        medico.setClinica(clinica);

        gradeHorario = new GradeHorario();
        gradeHorario.setId(gradeId);
        gradeHorario.setMedico(medico);
        gradeHorario.setDiaSemana(1);
        gradeHorario.setHoraInicio(LocalTime.of(8, 0));
        gradeHorario.setHoraFim(LocalTime.of(12, 0));
    }

    @Test
    @DisplayName("Deve criar grade de horário com sucesso")
    void shouldCreateGradeHorario() {
        when(medicoRepository.findByIdAndClinicaId(medicoId, clinicId)).thenReturn(medico);
        when(gradeHorarioRepository.save(any(GradeHorario.class))).thenReturn(gradeHorario);

        GradeHorario novaGrade = new GradeHorario();
        novaGrade.setDiaSemana(1);
        
        GradeHorario result = gradeHorarioService.create(clinicId, medicoId, novaGrade);

        assertNotNull(result);
        assertEquals(gradeId, result.getId());
        assertEquals(medicoId, result.getMedico().getId());
        verify(medicoRepository).findByIdAndClinicaId(medicoId, clinicId);
        verify(gradeHorarioRepository).save(novaGrade);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar grade para médico inexistente ou de outra clínica")
    void shouldThrowNotFoundWhenCreateWithInvalidMedico() {
        when(medicoRepository.findByIdAndClinicaId(medicoId, clinicId)).thenReturn(null);

        GradeHorario novaGrade = new GradeHorario();
        
        assertThrows(ResourceNotFoundException.class, 
            () -> gradeHorarioService.create(clinicId, medicoId, novaGrade));
            
        verify(gradeHorarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar todas as grades da clínica")
    void shouldFindAllByClinicId() {
        when(gradeHorarioRepository.findAllByMedico_ClinicaId(clinicId)).thenReturn(List.of(gradeHorario));

        List<GradeHorario> result = gradeHorarioService.findAllByClinicId(clinicId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(gradeHorarioRepository).findAllByMedico_ClinicaId(clinicId);
    }

    @Test
    @DisplayName("Deve retornar grade por ID e ID da clínica")
    void shouldFindByIdAndClinicId() {
        when(gradeHorarioRepository.findByIdAndMedico_ClinicaId(gradeId, clinicId)).thenReturn(Optional.of(gradeHorario));

        GradeHorario result = gradeHorarioService.findByIdAndClinicId(gradeId, clinicId);

        assertNotNull(result);
        assertEquals(gradeId, result.getId());
        verify(gradeHorarioRepository).findByIdAndMedico_ClinicaId(gradeId, clinicId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar grade inexistente na clínica")
    void shouldThrowNotFoundWhenFindByIdAndClinicId() {
        when(gradeHorarioRepository.findByIdAndMedico_ClinicaId(gradeId, clinicId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> gradeHorarioService.findByIdAndClinicId(gradeId, clinicId));
    }

    @Test
    @DisplayName("Deve deletar grade com sucesso")
    void shouldDeleteGradeHorario() {
        when(gradeHorarioRepository.findByIdAndMedico_ClinicaId(gradeId, clinicId)).thenReturn(Optional.of(gradeHorario));
        doNothing().when(gradeHorarioRepository).delete(gradeHorario);

        gradeHorarioService.delete(gradeId, clinicId);

        verify(gradeHorarioRepository).findByIdAndMedico_ClinicaId(gradeId, clinicId);
        verify(gradeHorarioRepository).delete(gradeHorario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar grade inexistente")
    void shouldThrowNotFoundWhenDeleteInvalidGrade() {
        when(gradeHorarioRepository.findByIdAndMedico_ClinicaId(gradeId, clinicId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> gradeHorarioService.delete(gradeId, clinicId));
            
        verify(gradeHorarioRepository, never()).delete(any());
    }
}