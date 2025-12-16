package com.multiclinicas.api.dtos;

import java.time.LocalTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GradeHorarioCreateDTO(
    @NotNull(message = "O ID do médico é obrigatório")
    Long medicoId,

    @NotNull(message = "O dia da semana é obrigatório")
    @Min(0) @Max(6)
    Integer diaSemana,

    @NotNull(message = "A hora de início é obrigatória")
    LocalTime horaInicio,

    @NotNull(message = "A hora de fim é obrigatória")
    LocalTime horaFim
) {
}