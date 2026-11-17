package org.kata.store.service;

import org.kata.store.domain.User;
import org.kata.store.repository.UserRepository;

import java.util.Optional;

/*
 * TODO: Alta de usuarios.
 *
 * Requisitos (escribe primero el test de cada uno):
 *   - Se puede dar de alta un usuario con id, nombre y apellidos.
 *   - No se puede dar de alta un usuario con un id que ya existe: se lanza una excepción.
 *
 * Pista: recibe el UserRepository por constructor. Así, en el test, puedes pasarle un mock.
 */
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {
        if(userRepository.findById(user.getId()).isPresent()) {
            throw new RuntimeException("User with same id already exists");
        }
        return userRepository.save(user);
    }
}
