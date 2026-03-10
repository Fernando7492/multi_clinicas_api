package com.multiclinicas.api.jobs;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.multiclinicas.api.models.Agendamento;
import com.multiclinicas.api.models.enums.StatusAgendamento;
import com.multiclinicas.api.repositories.AgendamentoRepository;
import com.multiclinicas.api.services.EmailService;

@Component
public class LembreteAgendamentoJob {

    private static final Logger log = LoggerFactory.getLogger(LembreteAgendamentoJob.class);

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "0 0 8 * * *")
    public void dispararLembretesDeConsulta() {
        LocalDate dataAmanha = LocalDate.now().plusDays(1);
        log.info("Iniciando rotina de lembretes para agendamentos do dia: {}", dataAmanha);
        
        List<Agendamento> agendamentos = agendamentoRepository
                .findByDataConsultaAndStatus(dataAmanha, StatusAgendamento.AGENDADO);

        if (agendamentos.isEmpty()) {
            log.info("Nenhum agendamento encontrado para amanhã.");
            return;
        }

        DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Agendamento agendamento : agendamentos) {
            try {
                String emailPaciente = agendamento.getPaciente().getEmail();
                
                if (emailPaciente == null || emailPaciente.isBlank()) {
                    continue;
                }

                String nomePaciente = agendamento.getPaciente().getNome();
                String nomeMedico = agendamento.getMedico().getNome();
                String dataFormatada = agendamento.getDataConsulta().format(formatadorData);
                String horaInicio = agendamento.getHoraInicio().toString();

                String assunto = "Lembrete de Consulta - Multi Clínicas";
                String mensagem = String.format(
                    "Olá, %s!\n\nEste é um lembrete da sua consulta agendada para amanhã (%s) às %s com o(a) Dr(a). %s.\n\nPor favor, chegue com 15 minutos de antecedência.\n\nAtenciosamente,\nEquipe Multi Clínicas",
                    nomePaciente, dataFormatada, horaInicio, nomeMedico
                );

                emailService.enviarEmail(emailPaciente, assunto, mensagem);

            } catch (Exception e) {
                log.error("Erro ao enviar lembrete para o agendamento ID: " + agendamento.getId(), e);
            }
        }
        
        log.info("Rotina finalizada. Foram processados {} agendamentos.", agendamentos.size());
    }
}