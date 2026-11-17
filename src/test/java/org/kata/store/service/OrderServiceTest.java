package org.kata.store.service;

import org.junit.jupiter.api.Test;
import org.kata.store.domain.Order;
import org.kata.store.domain.OrderStatus;
import org.kata.store.domain.User;
import org.kata.store.repository.OrderRepository;
import org.kata.store.repository.UserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        when(orderRepository.save(order)).thenReturn(order);

        // Act
        Order result = orderService.createOrder(order);

        // Assert
        assertEquals(OrderStatus.DRAFT, result.getStatus());
        assertEquals(1.0f, result.getPrice());
        verify(orderRepository).save(user);
    }
}
