package org.kata.store.service;

import org.kata.store.domain.User;
import org.kata.store.repository.UserRepository;

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

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(Long id) {
        return userRepository.findById(id);
    }

    public User registerUser(User user) {
        return userRepository.save(user);
    }
}
