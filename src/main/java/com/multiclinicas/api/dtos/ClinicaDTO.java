package com.multiclinicas.api.dtos;

import java.time.LocalDateTime;

public record ClinicaDTO(
        Long id,
        String nomeFantasia,
        String subdominio,
        Boolean ativo,
        LocalDateTime createdAt) {
}
