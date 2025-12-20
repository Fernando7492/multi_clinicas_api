package com.multiclinicas.api.dtos;

public record EspecialidadeDTO(
        Long id,
        String nome,
        Long clinicaId,
        String nomeClinica
) {}
