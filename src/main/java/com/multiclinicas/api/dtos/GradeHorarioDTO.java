package com.multiclinicas.api.dtos;

import java.time.LocalTime;

public record GradeHorarioDTO(
    Long id,
    Long medicoId,
    Integer diaSemana,
    LocalTime horaInicio,
    LocalTime horaFim
) {
}