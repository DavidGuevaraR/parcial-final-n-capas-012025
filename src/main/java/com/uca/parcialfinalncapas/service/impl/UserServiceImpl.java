package com.uca.parcialfinalncapas.service.impl;

import com.uca.parcialfinalncapas.dto.request.AuthRequest;
import com.uca.parcialfinalncapas.dto.request.UserCreateRequest;
import com.uca.parcialfinalncapas.dto.request.UserUpdateRequest;
import com.uca.parcialfinalncapas.dto.response.AuthResponse;
import com.uca.parcialfinalncapas.dto.response.UserResponse;
import com.uca.parcialfinalncapas.exceptions.UserNotFoundException;
import com.uca.parcialfinalncapas.repository.UserRepository;
import com.uca.parcialfinalncapas.service.UserService;
import com.uca.parcialfinalncapas.utils.JwtUtils;
import com.uca.parcialfinalncapas.utils.mappers.UserMapper;
import lombok.AllArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public UserResponse findByCorreo(String correo) {
        return UserMapper.toDTO(userRepository.findByCorreo(correo)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con correo: " + correo)));
    }

    @Override
    public UserResponse save(UserCreateRequest user) {

        if (userRepository.findByCorreo(user.getCorreo()).isPresent()) {
            throw new UserNotFoundException("Ya existe un usuario con el correo: " + user.getCorreo());
        }

        // Codifica el password antes de guardar
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return UserMapper.toDTO(userRepository.save(UserMapper.toEntityCreate(user)));
    }

    @Override
    public UserResponse update(UserUpdateRequest user) {
        if (userRepository.findById(user.getId()).isEmpty()) {
            throw new UserNotFoundException("No se encontró un usuario con el ID: " + user.getId());
        }

        return UserMapper.toDTO(userRepository.save(UserMapper.toEntityUpdate(user)));
    }

    @Override
    public void delete(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new UserNotFoundException("No se encontró un usuario con el ID: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public List<UserResponse> findAll() {
        return UserMapper.toDTOList(userRepository.findAll());
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        var user = userRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserNotFoundException("Usuario y/o contraseña incorrecta");
        }

        String token = jwtUtils.generateToken(user.getCorreo());
        return UserMapper.toLoginResponse(user, token);
    }
}
