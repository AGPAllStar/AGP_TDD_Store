package org.kata.store.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

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
        User user = null;

        try {
            // Act
            user = new User(null, "Apellidos");
            fail("Should have thrown an IllegalArgumentException");
        }catch(IllegalArgumentException e) {
            // Assert
            assertEquals("Name cannot be null", e.getMessage());
        }

        assertNull(user);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\n", "\t", "\r", " \n\t\r"})
    void shouldNotCreateUserWithNameEmpty(String newName) {
        // Arrange
        User user = null;

        try {
            // Act
            user = new User(newName, "Apellidos");
            fail("Should have thrown an IllegalArgumentException");
        }catch(IllegalArgumentException e) {
            // Assert
            assertEquals("Name cannot be empty", e.getMessage());
        }

        assertNull(user);
    }

    @Test
    void shouldNotCreateUserWithSurnameNull() {
        // Arrange
        User user = null;

        try {
            // Act
            user = new User("Nombre", null);
            fail("Should have thrown an IllegalArgumentException");
        }catch(IllegalArgumentException e) {
            // Assert
            assertEquals("Surname cannot be null", e.getMessage());
        }

        assertNull(user);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\n", "\t", "\r", " \n\t\r"})
    void shouldNotCreateUserWithSurnameEmpty(String newSurname) {
        // Arrange
        User user = null;

        try {
            // Act
            user = new User("Nombre", newSurname);
            fail("Should have thrown an IllegalArgumentException");
        }catch(IllegalArgumentException e) {
            // Assert
            assertEquals("Surname cannot be empty", e.getMessage());
        }

        assertNull(user);
    }
}
