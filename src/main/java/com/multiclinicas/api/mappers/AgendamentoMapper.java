package com.multiclinicas.api.mappers;

import org.springframework.stereotype.Component;

import com.multiclinicas.api.dtos.AgendamentoDTO;
import com.multiclinicas.api.models.Agendamento;

@Component
public class AgendamentoMapper {

    public AgendamentoDTO toDTO(Agendamento agendamento) {
        if (agendamento == null)
            return null;

        String nomePlano = (agendamento.getPlanoSaude() != null) ? agendamento.getPlanoSaude().getNome() : null;

        return new AgendamentoDTO(
                agendamento.getId(),
                agendamento.getPaciente().getId(),
                agendamento.getPaciente().getNome(),
                agendamento.getMedico().getId(),
                agendamento.getMedico().getNome(),
                agendamento.getDataConsulta(),
                agendamento.getHoraInicio(),
                agendamento.getHoraFim(),
                agendamento.getStatus(),
                agendamento.getTipoPagamento(),
                nomePlano,
                agendamento.getTokenAutorizacao(),
                agendamento.getObservacoes());
    }
}