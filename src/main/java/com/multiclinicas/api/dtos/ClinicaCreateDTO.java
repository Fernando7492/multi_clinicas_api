package com.multiclinicas.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ClinicaCreateDTO(
                @NotBlank(message = "O nome fantasia é obrigatório") String nomeFantasia,

                @NotBlank(message = "O subdomínio é obrigatório") @Pattern(regexp = "^[a-z0-9-]+$", message = "Subdomínio deve conter apenas letras minúsculas, números e hífens") String subdominio,

                Boolean ativo) {
}
