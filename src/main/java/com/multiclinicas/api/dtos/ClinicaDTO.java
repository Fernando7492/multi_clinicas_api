package com.multiclinicas.api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ClinicaDTO(
        @Schema(description = "ID único da clínica", example = "1") Long id,

        @Schema(description = "Nome fantasia da clínica", example = "Clínica Saúde Total") String nomeFantasia,

        @Schema(description = "Subdomínio para acesso", example = "saude-total") String subdominio,

        @Schema(description = "Status da clínica", example = "true") Boolean ativo,

        @Schema(description = "Data de criação do registro") LocalDateTime createdAt) {
}
