package com.multiclinicas.api.services;

import com.multiclinicas.api.models.Especialidade;
import java.util.List;
import java.util.Set;

public interface EspecialidadeService {
    //Buscar todas as especialidades presentes em uma clínica
    List<Especialidade> findAllByClinicId(Long clinicId);

    //Buscar uma especialidade especifica por id e clinica
    Especialidade findByIdAndClinicId(Long id, Long clinicId);

    //Cria uma nova especialidade para a clínica
    Especialidade create(Long clinicId, Especialidade especialidade);

    // Atualizar uma especialidade já existente
    Especialidade update(Long id, Long clinicId, Especialidade especialidade);

    // Apagar uma especialidade
    void delete(Long id, Long clinicId);

    //Buscar múltiplas especialidades ( no caso do médico ser multi especialista)
    Set<Especialidade> findByIdsAndClinicId(Set<Long> ids, Long clinicId);
}
