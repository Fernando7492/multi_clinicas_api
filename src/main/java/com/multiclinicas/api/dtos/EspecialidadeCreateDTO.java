package com.multiclinicas.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EspecialidadeCreateDTO(
    @NotBlank(message = "O nome da especialidade é obrigatório")
    @Size(min=5, max = 35, message = "O nome da especialidade deve ter entre 5 a 50 caracteres")
    String nome
){}