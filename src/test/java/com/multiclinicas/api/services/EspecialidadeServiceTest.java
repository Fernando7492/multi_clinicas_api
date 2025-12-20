package com.multiclinicas.api.services;

import com.multiclinicas.api.exceptions.BusinessException;
import com.multiclinicas.api.exceptions.ResourceNotFoundException;
import com.multiclinicas.api.models.Clinica;
import com.multiclinicas.api.models.Especialidade;
import com.multiclinicas.api.repositories.ClinicaRepository;
import com.multiclinicas.api.repositories.EspecialidadeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EspecialidadeServiceTest {

    private static final Long CLINIC_ID = 1L;

    @Mock
    private EspecialidadeRepository especialidadeRepository;

    @Mock
    private ClinicaRepository clinicaRepository;

    @InjectMocks
    private EspecialidadeServiceImpl especialidadeService;

    @Test
    @DisplayName("Deve retornar todas as especialidades de uma clínica")
    void shouldReturnAllEspecialidadesByClinicId() {

        Especialidade esp1 = new Especialidade();
        esp1.setId(1L);
        esp1.setNome("Cardiologia");

        Especialidade esp2 = new Especialidade();
        esp2.setId(2L);
        esp2.setNome("Pediatria");

        when(especialidadeRepository.findByClinicaId(CLINIC_ID))
                .thenReturn(List.of(esp1, esp2));

        List<Especialidade> result = especialidadeService.findAllByClinicId(CLINIC_ID);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Especialidade::getNome)
                .containsExactlyInAnyOrder("Cardiologia", "Pediatria");
        verify(especialidadeRepository).findByClinicaId(CLINIC_ID);
    }

    @Test
    @DisplayName("Deve retornar especialidade por ID quando existir")
    void shouldReturnEspecialidadeByIdWhenExists() {
        Long id = 1L;
        Especialidade especialidade = new Especialidade();
        especialidade.setId(id);
        especialidade.setNome("Cardiologia");

        when(especialidadeRepository.findByIdAndClinicaId(id, CLINIC_ID))
                .thenReturn(Optional.of(especialidade));

        Especialidade result = especialidadeService.findByIdAndClinicId(id, CLINIC_ID);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getNome()).isEqualTo("Cardiologia");
        verify(especialidadeRepository).findByIdAndClinicaId(id, CLINIC_ID);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar especialidade inexistente por ID")
    void shouldThrowExceptionWhenEspecialidadeNotFoundById() {

        Long id = 1L;

        when(especialidadeRepository.findByIdAndClinicaId(id, CLINIC_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> especialidadeService.findByIdAndClinicId(id, CLINIC_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Especialidade não encontrada");
    }

    @Test
    @DisplayName("Deve criar especialidade com sucesso")
    void shouldCreateEspecialidadeSuccessfully() {

        Clinica clinica = new Clinica();
        clinica.setId(CLINIC_ID);
        clinica.setNomeFantasia("Clínica Saúde Total");

        Especialidade especialidade = new Especialidade();
        especialidade.setNome("Cardiologia");

        Especialidade especialidadeSalva = new Especialidade();
        especialidadeSalva.setId(1L);
        especialidadeSalva.setNome("Cardiologia");
        especialidadeSalva.setClinica(clinica);

        when(clinicaRepository.findById(CLINIC_ID)).thenReturn(Optional.of(clinica));
        when(especialidadeRepository.existsByNomeIgnoreCaseAndClinicaId("Cardiologia", CLINIC_ID))
                .thenReturn(false);
        when(especialidadeRepository.save(any(Especialidade.class)))
                .thenReturn(especialidadeSalva);

        Especialidade result = especialidadeService.create(CLINIC_ID, especialidade);

        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("Cardiologia");
        assertThat(result.getClinica()).isEqualTo(clinica);

        verify(clinicaRepository).findById(CLINIC_ID);
        verify(especialidadeRepository).existsByNomeIgnoreCaseAndClinicaId("Cardiologia", CLINIC_ID);
        verify(especialidadeRepository).save(any(Especialidade.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar especialidade se a clínica não existir")
    void shouldThrowExceptionWhenCreatingEspecialidadeIfClinicaNotFound() {

        Especialidade especialidade = new Especialidade();
        especialidade.setNome("Cardiologia");

        when(clinicaRepository.findById(CLINIC_ID)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> especialidadeService.create(CLINIC_ID, especialidade))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Clínica não encontrada");

        verify(especialidadeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar especialidade duplicada (case-insensitive)")
    void shouldThrowExceptionWhenCreatingDuplicateEspecialidade() {

        Clinica clinica = new Clinica();
        clinica.setId(CLINIC_ID);

        Especialidade especialidade = new Especialidade();
        especialidade.setNome("CARDIOLOGIA");

        when(clinicaRepository.findById(CLINIC_ID)).thenReturn(Optional.of(clinica));
        when(especialidadeRepository.existsByNomeIgnoreCaseAndClinicaId("Cardiologia", CLINIC_ID))
                .thenReturn(true);

        assertThatThrownBy(() -> especialidadeService.create(CLINIC_ID, especialidade))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Já existe uma especialidade com o nome");

        verify(especialidadeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve normalizar nome ao criar especialidade")
    void shouldNormalizeNameWhenCreatingEspecialidade() {
        Clinica clinica = new Clinica();
        clinica.setId(CLINIC_ID);

        Especialidade especialidade = new Especialidade();
        especialidade.setNome("cardiologia clínica");

        when(clinicaRepository.findById(CLINIC_ID)).thenReturn(Optional.of(clinica));
        when(especialidadeRepository.existsByNomeIgnoreCaseAndClinicaId("Cardiologia Clínica", CLINIC_ID))
                .thenReturn(false);
        when(especialidadeRepository.save(any(Especialidade.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        especialidadeService.create(CLINIC_ID, especialidade);

        assertThat(especialidade.getNome()).isEqualTo("Cardiologia Clínica");
        verify(especialidadeRepository).save(especialidade);
    }

    @Test
    @DisplayName("Deve atualizar especialidade com sucesso")
    void shouldUpdateEspecialidadeSuccessfully() {

        Long id = 1L;

        Especialidade existente = new Especialidade();
        existente.setId(id);
        existente.setNome("Cardiologia");

        Especialidade atualizada = new Especialidade();
        atualizada.setNome("Cardiologia Clínica");

        when(especialidadeRepository.findByIdAndClinicaId(id, CLINIC_ID))
                .thenReturn(Optional.of(existente));
        when(especialidadeRepository.existsByNomeIgnoreCaseAndClinicaId("Cardiologia Clínica", CLINIC_ID))
                .thenReturn(false);
        when(especialidadeRepository.save(any(Especialidade.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Especialidade result = especialidadeService.update(id, CLINIC_ID, atualizada);

        assertThat(result.getNome()).isEqualTo("Cardiologia Clínica");
        verify(especialidadeRepository).findByIdAndClinicaId(id, CLINIC_ID);
        verify(especialidadeRepository).save(existente);
    }

    @Test
    @DisplayName("Deve permitir atualizar mantendo o mesmo nome (case-insensitive)")
    void shouldAllowUpdateWithSameNameDifferentCase() {

        Long id = 1L;

        Especialidade existente = new Especialidade();
        existente.setId(id);
        existente.setNome("Cardiologia");

        Especialidade atualizada = new Especialidade();
        atualizada.setNome("CARDIOLOGIA");

        when(especialidadeRepository.findByIdAndClinicaId(id, CLINIC_ID))
                .thenReturn(Optional.of(existente));
        when(especialidadeRepository.save(any(Especialidade.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Especialidade result = especialidadeService.update(id, CLINIC_ID, atualizada);

        assertThat(result.getNome()).isEqualTo("Cardiologia");
        verify(especialidadeRepository).save(existente);
        verify(especialidadeRepository, never()).existsByNomeIgnoreCaseAndClinicaId(any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar para nome que já existe")
    void shouldThrowExceptionWhenUpdatingToDuplicateName() {

        Long id = 1L;

        Especialidade existente = new Especialidade();
        existente.setId(id);
        existente.setNome("Cardiologia");

        Especialidade atualizada = new Especialidade();
        atualizada.setNome("Pediatria");

        when(especialidadeRepository.findByIdAndClinicaId(id, CLINIC_ID))
                .thenReturn(Optional.of(existente));
        when(especialidadeRepository.existsByNomeIgnoreCaseAndClinicaId("Pediatria", CLINIC_ID))
                .thenReturn(true);

        assertThatThrownBy(() -> especialidadeService.update(id, CLINIC_ID, atualizada))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Já existe uma especialidade com o nome");

        verify(especialidadeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar especialidade inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentEspecialidade() {

        Long id = 1L;
        Especialidade atualizada = new Especialidade();
        atualizada.setNome("Cardiologia");

        when(especialidadeRepository.findByIdAndClinicaId(id, CLINIC_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> especialidadeService.update(id, CLINIC_ID, atualizada))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve deletar especialidade com sucesso")
    void shouldDeleteEspecialidadeSuccessfully() {

        Long id = 1L;
        Especialidade especialidade = new Especialidade();
        especialidade.setId(id);

        when(especialidadeRepository.findByIdAndClinicaId(id, CLINIC_ID))
                .thenReturn(Optional.of(especialidade));

        especialidadeService.delete(id, CLINIC_ID);

        verify(especialidadeRepository).delete(especialidade);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar especialidade inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentEspecialidade() {
        Long id = 1L;

        when(especialidadeRepository.findByIdAndClinicaId(id, CLINIC_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> especialidadeService.delete(id, CLINIC_ID))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(especialidadeRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve buscar múltiplas especialidades por IDs")
    void shouldFindMultipleEspecialidadesByIds() {

        Set<Long> ids = Set.of(1L, 2L);

        Especialidade esp1 = new Especialidade();
        esp1.setId(1L);
        esp1.setNome("Cardiologia");

        Especialidade esp2 = new Especialidade();
        esp2.setId(2L);
        esp2.setNome("Pediatria");

        when(especialidadeRepository.findByIdAndClinicaId(1L, CLINIC_ID))
                .thenReturn(Optional.of(esp1));
        when(especialidadeRepository.findByIdAndClinicaId(2L, CLINIC_ID))
                .thenReturn(Optional.of(esp2));

        Set<Especialidade> result = especialidadeService.findByIdsAndClinicId(ids, CLINIC_ID);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Especialidade::getNome)
                .containsExactlyInAnyOrder("Cardiologia", "Pediatria");
    }

    @Test
    @DisplayName("Deve lançar exceção quando conjunto de IDs é vazio")
    void shouldThrowExceptionWhenIdsSetIsEmpty() {

        Set<Long> ids = new HashSet<>();

        assertThatThrownBy(() -> especialidadeService.findByIdsAndClinicId(ids, CLINIC_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("É necessário informar pelo menos uma especialidade");
    }

    @Test
    @DisplayName("Deve lançar exceção quando conjunto de IDs é null")
    void shouldThrowExceptionWhenIdsSetIsNull() {
        assertThatThrownBy(() -> especialidadeService.findByIdsAndClinicId(null, CLINIC_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("É necessário informar pelo menos uma especialidade");
    }

    @Test
    @DisplayName("Deve aceitar especialidade com 5 caracteres exatos")
    void shouldAcceptNameWithExactly5Characters() {
        Clinica clinica = new Clinica();
        clinica.setId(CLINIC_ID);

        Especialidade especialidade = new Especialidade();
        especialidade.setNome("Neuro");

        when(clinicaRepository.findById(CLINIC_ID)).thenReturn(Optional.of(clinica));
        when(especialidadeRepository.existsByNomeIgnoreCaseAndClinicaId("Neuro", CLINIC_ID))
                .thenReturn(false);
        when(especialidadeRepository.save(any(Especialidade.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Especialidade result = especialidadeService.create(CLINIC_ID, especialidade);

        assertThat(result.getNome()).isEqualTo("Neuro");
        verify(especialidadeRepository).save(any());
    }

    @Test
    @DisplayName("Deve aceitar especialidade com 35 caracteres exatos")
    void shouldAcceptNameWithExactly35Characters() {

        Clinica clinica = new Clinica();
        clinica.setId(CLINIC_ID);

        Especialidade especialidade = new Especialidade();
        especialidade.setNome("Cardiologia Intervencionista Clínic");

        when(clinicaRepository.findById(CLINIC_ID)).thenReturn(Optional.of(clinica));
        when(especialidadeRepository.existsByNomeIgnoreCaseAndClinicaId("Cardiologia Intervencionista Clínic", CLINIC_ID))
                .thenReturn(false);
        when(especialidadeRepository.save(any(Especialidade.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Especialidade result = especialidadeService.create(CLINIC_ID, especialidade);

        assertThat(result).isNotNull();
        verify(especialidadeRepository).save(any());
    }

    @Test
    @DisplayName("Deve aceitar especialidade com acentuação")
    void shouldAcceptNameWithAccents() {
        Clinica clinica = new Clinica();
        clinica.setId(CLINIC_ID);

        Especialidade especialidade = new Especialidade();
        especialidade.setNome("Cirurgia Plástica");

        when(clinicaRepository.findById(CLINIC_ID)).thenReturn(Optional.of(clinica));
        when(especialidadeRepository.existsByNomeIgnoreCaseAndClinicaId("Cirurgia Plástica", CLINIC_ID))
                .thenReturn(false);
        when(especialidadeRepository.save(any(Especialidade.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Especialidade result = especialidadeService.create(CLINIC_ID, especialidade);

        assertThat(result).isNotNull();
        verify(especialidadeRepository).save(any());
    }
}