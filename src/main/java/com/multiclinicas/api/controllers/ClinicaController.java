package com.multiclinicas.api.controllers;

import com.multiclinicas.api.dtos.ClinicaCreateDTO;
import com.multiclinicas.api.dtos.ClinicaDTO;
import com.multiclinicas.api.mappers.ClinicaMapper;
import com.multiclinicas.api.models.Clinica;
import com.multiclinicas.api.services.ClinicaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clinicas")
public class ClinicaController {

    @Autowired
    private ClinicaService clinicaService;

    @Autowired
    private ClinicaMapper clinicaMapper;

    @GetMapping
    public ResponseEntity<List<ClinicaDTO>> getAllClinicas() {
        List<Clinica> clinicas = clinicaService.findAll();
        List<ClinicaDTO> dtos = clinicas.stream()
                .map(clinicaMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClinicaDTO> getClinicaById(@PathVariable Long id) {
        Clinica clinica = clinicaService.findById(id);
        return ResponseEntity.ok(clinicaMapper.toDTO(clinica));
    }

    @PostMapping
    public ResponseEntity<ClinicaDTO> createClinica(@RequestBody @Valid ClinicaCreateDTO clinicaCreateDTO) {
        Clinica clinica = clinicaMapper.toEntity(clinicaCreateDTO);
        Clinica savedClinica = clinicaService.create(clinica);
        return ResponseEntity.status(HttpStatus.CREATED).body(clinicaMapper.toDTO(savedClinica));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClinicaDTO> updateClinica(@PathVariable Long id,
            @RequestBody @Valid ClinicaCreateDTO clinicaCreateDTO) {
        Clinica clinica = clinicaMapper.toEntity(clinicaCreateDTO);
        Clinica updatedClinica = clinicaService.update(id, clinica);
        return ResponseEntity.ok(clinicaMapper.toDTO(updatedClinica));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClinica(@PathVariable Long id) {
        clinicaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
