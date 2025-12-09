package com.multiclinicas.api.mappers;

import org.springframework.stereotype.Component;

import com.multiclinicas.api.dtos.UsuarioAdminCreateDTO;
import com.multiclinicas.api.dtos.UsuarioAdminDTO;
import com.multiclinicas.api.models.UsuarioAdmin;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UsuarioAdminMapper {

    private final EnderecoMapper enderecoMapper;

    public UsuarioAdminDTO toDTO(UsuarioAdmin usuario) {
        if (usuario == null) {
            return null;
        }
        return new UsuarioAdminDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getCpf(),
            usuario.getTelefone(),
            usuario.getTelefoneSecundario(),
            usuario.getEmail(),
            usuario.getRole(),
            enderecoMapper.toDTO(usuario.getEndereco())
        );
    }

    public UsuarioAdmin toEntity(UsuarioAdminCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        UsuarioAdmin usuario = new UsuarioAdmin();
        usuario.setNome(dto.nome());
        usuario.setCpf(dto.cpf());
        usuario.setTelefone(dto.telefone());
        usuario.setTelefoneSecundario(dto.telefoneSecundario());
        usuario.setEmail(dto.email());
        usuario.setRole(dto.role());
        usuario.setSenhaHash(dto.senhaHash());
        usuario.setEndereco(enderecoMapper.toEntity(dto.endereco()));

        return usuario;
    }
}