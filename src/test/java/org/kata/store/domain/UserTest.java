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
        User user = new User();

        // Act
        user.setName("Nombre");
        user.setSurname("Apellidos");

        // Assert
        assertEquals("Nombre", user.getName());
        assertEquals("Apellidos", user.getSurname());
    }

    @Test
    void shouldNotCreateUserWithNameNull() {
        // Arrange
        User user = new User();

        try {
            // Act
            user.setName(null);
            fail("Should have thrown an IllegalArgumentException");
        }catch(IllegalArgumentException e) {
            // Assert
            assertEquals("Name cannot be null", e.getMessage());
        }

        // Assert
        assertNotNull(user.getName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\n", "\t", "\r", " \n\t\r"})
    void shouldNotCreateUserWithNameEmpty(String newName) {
        // Arrange
        User user = new User();

        try {
            // Act
            user.setName(newName);
            fail("Should have thrown an IllegalArgumentException");
        }catch(IllegalArgumentException e) {
            // Assert
            assertEquals("Name cannot be empty", e.getMessage());
        }

        // Assert
        assertFalse(user.getName().trim().isEmpty());
    }

    @Test
    void shouldNotCreateUserWithSurnameNull() {
        // Arrange
        User user = new User();

        try {
            // Act
            user.setSurname(null);
            fail("Should have thrown an IllegalArgumentException");
        }catch(IllegalArgumentException e) {
            // Assert
            assertEquals("Surname cannot be null", e.getMessage());
        }

        // Assert
        assertNotNull(user.getSurname());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\n", "\t", "\r", " \n\t\r"})
    void shouldNotCreateUserWithSurnameEmpty(String newSurname) {
        // Arrange
        User user = new User();

        try {
            // Act
            user.setSurname(newSurname);
            fail("Should have thrown an IllegalArgumentException");
        }catch(IllegalArgumentException e) {
            // Assert
            assertEquals("Surname cannot be empty", e.getMessage());
        }

        // Assert
        assertFalse(user.getSurname().trim().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 32L, 1000L, Long.MAX_VALUE})
    void shouldCreateUserWithPositiveId(long newId) {
        // Arrange
        User user = new User();

        // Act
        user.setId(newId);

        // Assert
        assertEquals(newId, user.getId());
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -32L, -1000L, Long.MIN_VALUE})
    void shouldNotCreateUserWithZeroOrNegativeId(long newId) {
        // Arrange
        User user = new User();

        try {
            // Act
            user.setId(newId);
            fail("Should have thrown an IllegalArgumentException");
        }catch(IllegalArgumentException e) {
            // Assert
            assertEquals("Id cannot be zero or negative", e.getMessage());
        }

        assertNotEquals(newId, user.getId());
    }
}
