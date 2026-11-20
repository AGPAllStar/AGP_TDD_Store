package org.kata.store.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kata.store.domain.User;
import org.kata.store.repository.UserRepository;
import org.kata.store.service.exceptions.RepeatedIdException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/*
 * Tests unitarios de UserService con Mockito.
 *
 * El UserRepository es una dependencia: aquí se sustituye por un mock, nunca por la implementación real.
 *
 * Ideas de tests:
 *   - shouldSaveNewUser                      -> verify(userRepository).save(...)
 *   - shouldRejectUserWithDuplicatedId       -> stub: when(...).thenReturn(true) + assertThrows
 *   - shouldNotSaveUserWithDuplicatedId      -> verify(userRepository, never()).save(any())
 *
 * Pista: puedes crear los mocks con mock(UserRepository.class) o con
 * @ExtendWith(MockitoExtension.class) y @Mock.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldSaveNewUser() {
        // Arrange
        userService = new UserService(userRepository);
        User user = new User(2L, "Juan", "Dominguez");
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User result = userService.registerUser(user);

        // Assert
        assertEquals(2L, result.getId());
        assertEquals("Juan", result.getName());
        assertEquals("Dominguez", result.getSurname());
        verify(userRepository).save(user);
    }

    @Test
    void shouldNotSaveUserWithRepeatedId() {
        // Arrange
        userService = new UserService(userRepository);
        User user = new User(2L, "Juan", "Dominguez");
        User user2 = new User(2L, "Juanes", "Domingo");
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        try {
            // Act
            userService.registerUser(user);
        }catch (RepeatedIdException e){
            // Assert
            assertEquals("User with same id already exists", e.getMessage());
        }

        // Assert
        verify(userRepository).findById(2L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldSaveUserWithDifferentIdButSameNameAndSurname() {
        // Arrange
        userService = new UserService(userRepository);
        User user = new User(1L, "Juan", "Dominguez");
        User user2 = new User(2L, "Juan", "Dominguez");
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User result = userService.registerUser(user);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Juan", result.getName());
        assertEquals("Dominguez", result.getSurname());
        verify(userRepository, never()).findById(2L);
        verify(userRepository).save(user);
    }
}
