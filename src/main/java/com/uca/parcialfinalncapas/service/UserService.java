package com.uca.parcialfinalncapas.service;

import com.uca.parcialfinalncapas.dto.request.AuthRequest;
import com.uca.parcialfinalncapas.dto.request.UserCreateRequest;
import com.uca.parcialfinalncapas.dto.request.UserUpdateRequest;
import com.uca.parcialfinalncapas.dto.response.AuthResponse;
import com.uca.parcialfinalncapas.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param correo el correo electrónico del usuario a buscar
     * @return un objeto User si se encuentra, o null si no existe
     */
    UserResponse findByCorreo(String correo);

    /**
     * Guarda un nuevo usuario en la base de datos.
     *
     * @param user el objeto User a guardar
     * @return el usuario guardado
     */
    UserResponse save(UserCreateRequest user);

    /**
     * Actualiza un usuario existente.
     *
     * @param user el objeto User con los datos actualizados
     * @return el usuario actualizado
     */
    UserResponse update(UserUpdateRequest user);

    /**
     * Elimina un usuario por su ID.
     *
     * @param id el ID del usuario a eliminar
     */
    void delete(Long id);

    /**
     * Buscar todos los usuarios.
     */
    List<UserResponse> findAll();

    /**
     * Realiza el inicio de sesión de un usuario.
     *
     * @param request objeto que contiene las credenciales del usuario
     * @return un objeto AuthResponse con el token de autenticación
     */
    AuthResponse login(AuthRequest request);

}
