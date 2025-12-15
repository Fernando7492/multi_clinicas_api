package com.multiclinicas.api.dtos;

import java.util.Set;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MedicoCreateDTO (
		@NotBlank(message = "O nome é obrigatório") String nome,
		@CPF
		@NotBlank(message = "O CPF é obrigatório") String cpf,
		@NotBlank(message = "O CRM é obrigatório") String crm,
		@NotBlank(message = "É necessário adicionar pelo menos um número de telefone") String telefone,
		@NotNull(message = "A duração da consulta é obrigatória") Integer duracaoConsulta,
		@NotNull(message = "É necessário adicionar pelo menos uma especialidade") Set<Long> especialidadeId,
		//@NotNull(message = "O endereço é obrigatório") EnderecoCreateDTO endereco,
		String telefoneSecundario,
		Boolean ativo) {

}