package com.multiclinicas.api.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiclinicas.api.exceptions.ResourceNotFoundException;
import com.multiclinicas.api.models.Clinica;
import com.multiclinicas.api.models.Endereco;
import com.multiclinicas.api.models.UsuarioAdmin;
import com.multiclinicas.api.repositories.ClinicaRepository;
import com.multiclinicas.api.repositories.UsuarioAdminRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioAdminServiceImpl implements UsuarioAdminService {

    private final UsuarioAdminRepository usuarioAdminRepository;
    private final ClinicaRepository clinicaRepository;

    private static final String USER_NOT_FOUND_MSG = "Usuário Admin não encontrado com o ID: ";
    private static final String CLINIC_NOT_FOUND_MSG = "Clínica não encontrada com o ID: ";

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioAdmin> findAllByClinicId(Long clinicId) {
        return usuarioAdminRepository.findAllByClinicaId(clinicId);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioAdmin findByIdAndClinicId(Long id, Long clinicId) {
        return usuarioAdminRepository.findByIdAndClinicaId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MSG + id));
    }

    @Override
    @Transactional
    public UsuarioAdmin createUsuarioAdmin(Long clinicId, UsuarioAdmin usuario) {
        Clinica clinica = clinicaRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException(CLINIC_NOT_FOUND_MSG + clinicId));
        usuario.setClinica(clinica);
        return usuarioAdminRepository.save(usuario);
    }

    @Override
    @Transactional
    public UsuarioAdmin updateUsuarioAdmin(Long id, Long clinicId, UsuarioAdmin usuarioDetails) {
        UsuarioAdmin usuarioExistente = findByIdAndClinicId(id, clinicId);
        usuarioExistente.setNome(usuarioDetails.getNome());
        usuarioExistente.setEmail(usuarioDetails.getEmail());
        usuarioExistente.setCpf(usuarioDetails.getCpf());
        usuarioExistente.setTelefone(usuarioDetails.getTelefone());
        usuarioExistente.setTelefoneSecundario(usuarioDetails.getTelefoneSecundario());
        usuarioExistente.setRole(usuarioDetails.getRole());

        if (usuarioDetails.getSenhaHash() != null && !usuarioDetails.getSenhaHash().isBlank()) {
            usuarioExistente.setSenhaHash(usuarioDetails.getSenhaHash());
        }

        if (usuarioDetails.getEndereco() != null) {
            Endereco enderecoExistente = usuarioExistente.getEndereco();
            if (enderecoExistente == null) {
                enderecoExistente = new Endereco();
                usuarioExistente.setEndereco(enderecoExistente);
            }
            Endereco enderecoNovo = usuarioDetails.getEndereco();
            enderecoExistente.setCep(enderecoNovo.getCep());
            enderecoExistente.setLogradouro(enderecoNovo.getLogradouro());
            enderecoExistente.setNumero(enderecoNovo.getNumero());
            enderecoExistente.setComplemento(enderecoNovo.getComplemento());
            enderecoExistente.setBairro(enderecoNovo.getBairro());
            enderecoExistente.setCidade(enderecoNovo.getCidade());
            enderecoExistente.setEstado(enderecoNovo.getEstado());
        }

        return usuarioAdminRepository.save(usuarioExistente);
    }

    @Override
    @Transactional
    public void delete(Long id, Long clinicId) {
        UsuarioAdmin usuario = findByIdAndClinicId(id, clinicId);
        usuarioAdminRepository.delete(usuario);
    }

    @Override
    public List<UsuarioAdmin> findAllByNomeContainsIgnoreCase(String nome) {
        throw new UnsupportedOperationException("Unimplemented method 'findAllByNomeContainsIgnoreCase'");
    }

    @Override
    public UsuarioAdmin findById(Long id) {
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }
}
