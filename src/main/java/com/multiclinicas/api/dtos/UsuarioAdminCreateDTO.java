package com.multiclinicas.api.dtos;

import com.multiclinicas.api.models.enums.Role;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioAdminCreateDTO(
        @NotBlank(message = "O nome é obrigatorio") String nome,
        @NotBlank(message = "O cpf é obrigatorio") String cpf,
        @NotBlank(message = "O telefone é obrigatorio") String telefone,
        String telefoneSecundario,
        @NotBlank(message = "O email é obrigatorio") String email,
        @NotBlank(message = "A senha é obrigatoria") String senhaHash,
        Role role,
        @Valid
        @NotNull(message = "O endereço é obrigatório")
        CreateEnderecoDTO endereco) {
}