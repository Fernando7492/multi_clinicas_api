package com.multiclinicas.api.services;

import java.util.List;

import com.multiclinicas.api.models.UsuarioAdmin;


public interface UsuarioAdminService {
    List<UsuarioAdmin> findAllByNomeContainsIgnoreCase(String nome);
    List<UsuarioAdmin> findAllByClinicId(Long clinicId);
    UsuarioAdmin findByIdAndClinicId(Long id,Long clinicId);

    UsuarioAdmin findById(Long id);

    UsuarioAdmin createUsuarioAdmin(Long clinicId, UsuarioAdmin usuario);

    UsuarioAdmin updateUsuarioAdmin(Long id, Long clinicId, UsuarioAdmin usuario);

    void delete(Long id, Long clinicId);

}
