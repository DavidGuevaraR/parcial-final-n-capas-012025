package com.uca.parcialfinalncapas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long idUsuario;
    private String nombre;
    private String correo;
    private String nombreRol;
}
