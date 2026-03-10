package com.multiclinicas.api.dtos;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record DisponibilidadeDTO(
        Long medicoId,
        LocalDate dataConsulta,
        List<LocalTime> horariosDisponiveis
){}
