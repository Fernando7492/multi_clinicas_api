package com.multiclinicas.api.repositories;

import com.multiclinicas.api.models.Especialidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EspecialidadeRepository extends JpaRepository <Especialidade, Long> {

    //Buscar as especialidades presentes em determinada clinica
    List<Especialidade> findByClinicaId(Long clinicaId);

    //Buscar a especialidade por id e id da clínica
    Optional<Especialidade> findByIdAndClinicaId(Long id, Long clinicaId);

    //Tratamento para Case sensitive (dentista == DENTISTA)
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Especialidade e WHERE e.clinica.id = :clinicaId AND LOWER(e.nome) = LOWER(:nome)")
    boolean existsByNomeIgnoreCaseAndClinicaId(@Param("nome") String nome, @Param("clinicaId") Long clinicaId);

}

