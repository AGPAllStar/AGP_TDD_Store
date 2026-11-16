package org.kata.store.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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
        assertEquals("Nombre", user.getName());
        assertEquals("Apellidos", user.getSurname());
    }

    @Test
    void shouldNotCreateUserWithNameNull() {
        // Arrange
        User user;

        try {
            // Act
            user = new User(null, "Apellidos");;
            fail("Should have thrown an IllegalArgumentException");
        }catch(IllegalArgumentException e) {
            // Assert
            assertEquals("The name cannot be null", e.getMessage());
        }
    }
}
