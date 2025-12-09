package com.multiclinicas.api.dtos;

import com.multiclinicas.api.models.enums.Role;

public record UsuarioAdminDTO (
    Long id,
    String nome,
    String cpf,
    String telefone,
    String telefoneSecundario,
    String email,
    Role role,
    EnderecoDTO endereco
){
}