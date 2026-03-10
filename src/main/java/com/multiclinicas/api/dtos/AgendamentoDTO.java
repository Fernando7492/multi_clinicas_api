package com.multiclinicas.api.dtos;

import java.time.LocalDate;
import java.time.LocalTime;

import com.multiclinicas.api.models.enums.StatusAgendamento;
import com.multiclinicas.api.models.enums.TipoPagamento;

public record AgendamentoDTO(
        Long id,
        Long pacienteId,
        String nomePaciente,
        Long medicoId,
        String nomeMedico,
        LocalDate dataConsulta,
        LocalTime horaInicio,
        LocalTime horaFim,
        StatusAgendamento status,
        TipoPagamento tipoPagamento,
        String nomePlanoSaude,
        String tokenAutorizacao,
        String observacoes) {
}