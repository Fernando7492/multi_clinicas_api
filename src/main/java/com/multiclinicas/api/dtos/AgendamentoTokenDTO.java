package com.multiclinicas.api.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AgendamentoTokenDTO {
	
	@NotBlank(message = "O token de autorização é obrigatório.")
	private String tokenAutorizacao;

}