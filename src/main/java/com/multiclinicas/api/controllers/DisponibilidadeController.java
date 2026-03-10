package com.multiclinicas.api.controllers;

import com.multiclinicas.api.config.tenant.TenantContext;
import com.multiclinicas.api.dtos.DisponibilidadeDTO;
import com.multiclinicas.api.services.AgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/agendamentos/disponibilidade")
@RequiredArgsConstructor
@Tag(name = "Disponibilidade", description = "Consulta de horário livres para agendamento")
@ApiResponses(value = {
        @ApiResponse(responseCode = "401", description = "Não Autenticado (Token ausente ou inválido)"),
        @ApiResponse(responseCode = "403", description = "Não Autorizado (Sem permissão de acesso ou Tenant Inativo)")
})
public class DisponibilidadeController {

    private final AgendamentoService agendamentoService;

    @Operation(summary = "Buscar disponibilidade", description = "Retorna os horários disponíveis de um médico em uma data")
    @GetMapping
    public ResponseEntity<DisponibilidadeDTO> buscarDisponibilidade(
            @RequestParam Long medicoId,
            @RequestParam("data") LocalDate data) {
        Long clinicId = TenantContext.getClinicId();
        DisponibilidadeDTO disponibilidade = agendamentoService.buscarDisponibilidade(medicoId, data, clinicId);
        return ResponseEntity.ok(disponibilidade);
    }
}
