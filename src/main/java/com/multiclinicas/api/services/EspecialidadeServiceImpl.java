package com.multiclinicas.api.services;

import com.multiclinicas.api.exceptions.BusinessException;
import com.multiclinicas.api.exceptions.ResourceNotFoundException;
import com.multiclinicas.api.models.Clinica;
import com.multiclinicas.api.models.Especialidade;

import com.multiclinicas.api.repositories.ClinicaRepository;
import com.multiclinicas.api.repositories.EspecialidadeRepository;
import lombok.RequiredArgsConstructor;
import java.util.HashSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EspecialidadeServiceImpl implements EspecialidadeService {

    private final EspecialidadeRepository especialidadeRepository;
    private final ClinicaRepository clinicaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Especialidade> findAllByClinicId(Long clinicId) {
        return especialidadeRepository.findByClinicaId(clinicId);
    }

    @Override
    @Transactional(readOnly = true)
    public Especialidade findByIdAndClinicId(Long id, Long clinicId){
        return especialidadeRepository.findByIdAndClinicaId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidade não encontrada para essa clínica"));
    }

    @Override
    @Transactional
    public Especialidade create(Long clinicId, Especialidade especialidade) {
        Clinica clinica = clinicaRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clínica não encontrada"));

        String nomeNormalizado = normalizarNome(especialidade.getNome());
        if (especialidadeRepository.existsByNomeIgnoreCaseAndClinicaId(nomeNormalizado, clinicId)) {
            throw new BusinessException(
                    "Já existe uma especialidade com o nome '" + nomeNormalizado + "' nesta clínica");
        }

        especialidade.setNome(nomeNormalizado);
        especialidade.setClinica(clinica);
        return especialidadeRepository.save(especialidade);
    }

    @Override
    @Transactional
    public Especialidade update(Long id, Long clinicId, Especialidade especialidadeAtualizada) {
        Especialidade especialidadeExistente = findByIdAndClinicId(id, clinicId);

        String nomeNormalizado = normalizarNome(especialidadeAtualizada.getNome());
        if (!especialidadeExistente.getNome().equalsIgnoreCase(nomeNormalizado) &&
                especialidadeRepository.existsByNomeIgnoreCaseAndClinicaId(nomeNormalizado, clinicId)) {
            throw new BusinessException(
                    "Já existe uma especialidade com o nome '" + nomeNormalizado + "' nesta clínica");
        }

        especialidadeExistente.setNome(nomeNormalizado);
        return especialidadeRepository.save(especialidadeExistente);
    }

    @Override
    @Transactional
    public void delete(Long id, Long clinicId) {
        Especialidade especialidade = findByIdAndClinicId(id, clinicId);
        especialidadeRepository.delete(especialidade);
    }

    private String normalizarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return nome;
        }

        String[] palavras = nome.trim().toLowerCase().split("\\s+");
        StringBuilder nomeNormalizado = new StringBuilder();

        for (String palavra : palavras) {
            if (!palavra.isEmpty()) {
                nomeNormalizado.append(Character.toUpperCase(palavra.charAt(0)))
                        .append(palavra.substring(1))
                        .append(" ");
            }
        }

        return nomeNormalizado.toString().trim();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Especialidade> findByIdsAndClinicId(Set<Long> ids, Long clinicId) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("É necessário informar pelo menos uma especialidade");
        }

        Set<Especialidade> especialidades = new HashSet<>();

        for (Long id : ids) {
            Especialidade especialidade = findByIdAndClinicId(id, clinicId);
            especialidades.add(especialidade);
        }

        return especialidades;
    }
}

