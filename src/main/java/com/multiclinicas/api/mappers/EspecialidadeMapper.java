package com.multiclinicas.api.mappers;

import com.multiclinicas.api.dtos.EspecialidadeCreateDTO;
import com.multiclinicas.api.dtos.EspecialidadeDTO;
import com.multiclinicas.api.models.Especialidade;
import org.springframework.stereotype.Component;

@Component
public class EspecialidadeMapper {

    public EspecialidadeDTO toDTO(Especialidade especialidade) {
        if (especialidade == null){
            return null;
        }

        return new EspecialidadeDTO(
                especialidade.getId(),
                especialidade.getNome(),
                especialidade.getClinica() != null ? especialidade.getClinica().getId() : null,
                especialidade.getClinica() != null ? especialidade.getClinica().getNomeFantasia() : null
        );
    }

    public Especialidade toEntity(EspecialidadeCreateDTO dto) {
        if (dto == null){
            return null;
        }

        Especialidade especialidade = new Especialidade();
        especialidade.setNome(dto.nome());

        return especialidade;
    }
}
