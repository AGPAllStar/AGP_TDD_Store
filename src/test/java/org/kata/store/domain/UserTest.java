package org.kata.store.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * TODO: Tests unitarios de User.
 *
 * Ideas de tests:
 *   - shouldCreateUserWithIdNameAndSurname
 *   - shouldRejectEmptyName
 *   - shouldRejectNullSurname
 *
 * Pista: assertThrows(...) te devuelve la excepción, así puedes comprobar también su mensaje.
 */
class UserTest {

    @Test
    void shouldCreateUserWithNameAndSurname() {
        // Arrange
        User user;

        // Act
        user = new User("Nombre", "Apellidos");

        // Assert
        assertEquals("Nombre", user.name);
        assertEquals("Apellidos", user.surname);
    }
}
