package com.multiclinicas.api.services;

import com.multiclinicas.api.models.Clinica;
import java.util.List;

public interface ClinicaService {
    List<Clinica> findAll();

    Clinica findById(Long id);

    Clinica create(Clinica clinica);

    Clinica update(Long id, Clinica clinica);

    void delete(Long id);
}
