package org.kata.store.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kata.store.domain.Order;
import org.kata.store.domain.OrderStatus;
import org.kata.store.domain.User;
import org.kata.store.repository.OrderRepository;
import org.kata.store.repository.UserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/*
 * TODO: Tests unitarios de OrderService con Mockito.
 *
 * UserRepository y OrderRepository son dependencias: se sustituyen por dobles de prueba.
 *
 * Ideas de tests:
 *   - shouldCreateOrderInDraftForExistingUser      -> stub del usuario existente + verify del save
 *   - shouldRejectOrderForUnknownUser              -> stub: existe = false + assertThrows
 *   - shouldNotSaveOrderWhenUserDoesNotExist       -> verify(orderRepository, never()).save(any())
 *   - shouldRejectOrderWithInvalidPrice
 *   - shouldAdvanceOrderToNextStatus
 *   - shouldRejectInvalidStatusTransition
 *
 * Recuerda la diferencia:
 *   - Stub: la dependencia devuelve un valor fijo y compruebas el resultado (estado).
 *   - Mock: compruebas con verify que el servicio llamó a la dependencia (comportamiento).
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldCreateOrderInDraftForExistingUser() {
        // Arrange
        userService = new UserService(userRepository);
        orderService = new OrderService(orderRepository, userService);
        Order order = new Order(1.0f);
        User user = new User(2L, "Juan", "Dominguez");
        order.setUserId(user.getId());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(orderRepository.save(order)).thenReturn(order);

        // Act
        Order result = orderService.createOrder(order);

        // Assert
        assertEquals(OrderStatus.DRAFT, result.getStatus());
        assertEquals(1.0f, result.getPrice());
        assertEquals(2L, result.getUserId());
        verify(orderRepository).save(order);
    }

    @Test
    void shouldNotCreateOrderForNotExistingUser() {
        // Arrange
        userService = new UserService(userRepository);
        orderService = new OrderService(orderRepository, userService);
        Order order = new Order(1.0f);
        order.setUserId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            // Act
            orderService.createOrder(order);
        }catch (RuntimeException e){
            // Assert
            assertEquals("It doesn't exist user with given id", e.getMessage());
        }

        // Assert
        verify(userRepository).findById(1L);
        verify(orderRepository, never()).save(order);
    }
}
