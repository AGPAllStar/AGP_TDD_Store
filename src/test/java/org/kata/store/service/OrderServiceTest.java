package org.kata.store.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kata.store.domain.Order;
import org.kata.store.domain.OrderStatus;
import org.kata.store.domain.User;
import org.kata.store.repository.OrderRepository;
import org.kata.store.repository.UserRepository;
import org.kata.store.service.exceptions.RepeatedIdException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

/*
 * Tests unitarios de OrderService con Mockito.
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
        Order order = new Order();
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
        Order order = new Order();
        order.setUserId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            // Act
            orderService.createOrder(order);
        }catch (RepeatedIdException e){
            // Assert
            assertEquals("It doesn't exist user with given id", e.getMessage());
        }

        // Assert
        verify(userRepository).findById(1L);
        verify(orderRepository, never()).save(order);
    }

    @Test
    void shouldProcessOrderInDraftState() {
        // Arrange
        userService = new UserService(userRepository);
        orderService = new OrderService(orderRepository, userService);
        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.update(order)).thenReturn(order);

        // Act
        Order result = orderService.processOrder(order.getId());

        // Assert
        assertEquals(OrderStatus.PROCESSED, result.getStatus());
        verify(orderRepository).update(order);
    }

    @Test
    void shouldDispatchOrderInProcessedState() {
        // Arrange
        userService = new UserService(userRepository);
        orderService = new OrderService(orderRepository, userService);
        Order order = new Order(OrderStatus.PROCESSED);
        order.setId(1L);
        order.setUserId(1L);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.update(order)).thenReturn(order);

        // Act
        Order result = orderService.dispatchOrder(order.getId());

        // Assert
        assertEquals(OrderStatus.DISPATCHED, result.getStatus());
        verify(orderRepository).update(order);
    }

    @Test
    void shouldDeliverOrderInDispatchedState() {
        // Arrange
        userService = new UserService(userRepository);
        orderService = new OrderService(orderRepository, userService);
        Order order = new Order(OrderStatus.DISPATCHED);
        order.setId(1L);
        order.setUserId(1L);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.update(order)).thenReturn(order);

        // Act
        Order result = orderService.deliverOrder(order.getId());

        // Assert
        assertEquals(OrderStatus.DELIVERED, result.getStatus());
        verify(orderRepository).update(order);
    }

    @Test
    void shouldNotProcessOrderInDispatchedState() {
        // Arrange
        long orderId = 1L;
        userService = new UserService(userRepository);
        orderService = new OrderService(orderRepository, userService);
        Order order = new Order(OrderStatus.DISPATCHED);
        order.setId(orderId);
        order.setUserId(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        try{
            // Act
            orderService.processOrder(orderId);
            fail("Should have thrown an IllegalStateException");
        }catch(IllegalStateException e) {
            // Assert
            assertEquals("The order cannot be processed", e.getMessage());
        }

        // Assert
        verify(orderRepository, never()).save(order);
    }

    @Test
    void shouldNotProcessOrderInProcessedState() {
        // Arrange
        long orderId = 1L;
        userService = new UserService(userRepository);
        orderService = new OrderService(orderRepository, userService);
        Order order = new Order(OrderStatus.PROCESSED);
        order.setId(orderId);
        order.setUserId(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        try{
            // Act
            orderService.processOrder(orderId);
            fail("Should have thrown an IllegalStateException");
        }catch(IllegalStateException e) {
            // Assert
            assertEquals("The order is already processed", e.getMessage());
        }

        // Assert
        verify(orderRepository, never()).save(order);
    }

    @Test
    void shouldNotProcessOrderInDeliveredState() {
        // Arrange
        long orderId = 1L;
        userService = new UserService(userRepository);
        orderService = new OrderService(orderRepository, userService);
        Order order = new Order(OrderStatus.DELIVERED);
        order.setId(orderId);
        order.setUserId(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        try{
            // Act
            orderService.processOrder(orderId);
            fail("Should have thrown an IllegalStateException");
        }catch(IllegalStateException e) {
            // Assert
            assertEquals("The order cannot be processed", e.getMessage());
        }

        // Assert
        verify(orderRepository, never()).save(order);
    }

    @Test
    void shouldNotDispatchOrderInDraftState() {
        // Arrange
        long orderId = 1L;
        userService = new UserService(userRepository);
        orderService = new OrderService(orderRepository, userService);
        Order order = new Order(OrderStatus.DRAFT);
        order.setId(orderId);
        order.setUserId(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        try{
            // Act
            orderService.dispatchOrder(orderId);
            fail("Should have thrown an IllegalStateException");
        }catch(IllegalStateException e) {
            // Assert
            assertEquals("The order cannot be dispatched", e.getMessage());
        }

        // Assert
        verify(orderRepository, never()).save(order);
    }

    @Test
    void shouldNotDispatchOrderInDispatchedState() {
        // Arrange
        long orderId = 1L;
        userService = new UserService(userRepository);
        orderService = new OrderService(orderRepository, userService);
        Order order = new Order(OrderStatus.DISPATCHED);
        order.setId(orderId);
        order.setUserId(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        try{
            // Act
            orderService.dispatchOrder(orderId);
            fail("Should have thrown an IllegalStateException");
        }catch(IllegalStateException e) {
            // Assert
            assertEquals("The order is already dispatched", e.getMessage());
        }

        // Assert
        verify(orderRepository, never()).save(order);
    }

    @Test
    void shouldNotDispatchOrderInDeliveredState() {
        // Arrange
        long orderId = 1L;
        userService = new UserService(userRepository);
        orderService = new OrderService(orderRepository, userService);
        Order order = new Order(OrderStatus.DELIVERED);
        order.setId(orderId);
        order.setUserId(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        try{
            // Act
            orderService.dispatchOrder(orderId);
            fail("Should have thrown an IllegalStateException");
        }catch(IllegalStateException e) {
            // Assert
            assertEquals("The order cannot be dispatched", e.getMessage());
        }

        // Assert
        verify(orderRepository, never()).save(order);
    }

    @Test
    void shouldNotDeliverOrderInDraftState() {
        // Arrange
        long orderId = 1L;
        userService = new UserService(userRepository);
        orderService = new OrderService(orderRepository, userService);
        Order order = new Order(OrderStatus.DRAFT);
        order.setId(orderId);
        order.setUserId(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        try{
            // Act
            orderService.deliverOrder(orderId);
            fail("Should have thrown an IllegalStateException");
        }catch(IllegalStateException e) {
            // Assert
            assertEquals("The order cannot be delivered", e.getMessage());
        }

        // Assert
        verify(orderRepository, never()).save(order);
    }

    @Test
    void shouldNotDeliverOrderInProcessedState() {
        // Arrange
        long orderId = 1L;
        userService = new UserService(userRepository);
        orderService = new OrderService(orderRepository, userService);
        Order order = new Order(OrderStatus.PROCESSED);
        order.setId(orderId);
        order.setUserId(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        try{
            // Act
            orderService.deliverOrder(orderId);
            fail("Should have thrown an IllegalStateException");
        }catch(IllegalStateException e) {
            // Assert
            assertEquals("The order cannot be delivered", e.getMessage());
        }

        // Assert
        verify(orderRepository, never()).save(order);
    }

    @Test
    void shouldNotDeliverOrderInDeliveredState() {
        // Arrange
        long orderId = 1L;
        userService = new UserService(userRepository);
        orderService = new OrderService(orderRepository, userService);
        Order order = new Order(OrderStatus.DELIVERED);
        order.setId(orderId);
        order.setUserId(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        try{
            // Act
            orderService.deliverOrder(orderId);
            fail("Should have thrown an IllegalStateException");
        }catch(IllegalStateException e) {
            // Assert
            assertEquals("The order is already delivered", e.getMessage());
        }

        // Assert
        verify(orderRepository, never()).save(order);
    }

    @Test
    void shouldNotProcessNotExistingOrder() {
        // Arrange
        long orderId = 1L;
        userService = new UserService(userRepository);
        orderService = new OrderService(orderRepository, userService);
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        try{
            // Act
            orderService.processOrder(orderId);
            fail("Should have thrown a NoSuchElementException");
        }catch(NoSuchElementException e) {
            // Assert
            assertEquals("Order with given id does not exist", e.getMessage());
        }

        // Assert
        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldNotDispatchNotExistingOrder() {
        // Arrange
        long orderId = 1L;
        userService = new UserService(userRepository);
        orderService = new OrderService(orderRepository, userService);
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        try{
            // Act
            orderService.dispatchOrder(orderId);
            fail("Should have thrown a NoSuchElementException");
        }catch(NoSuchElementException e) {
            // Assert
            assertEquals("Order with given id does not exist", e.getMessage());
        }

        // Assert
        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldNotDeliverNotExistingOrder() {
        // Arrange
        long orderId = 1L;
        userService = new UserService(userRepository);
        orderService = new OrderService(orderRepository, userService);
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        try{
            // Act
            orderService.deliverOrder(orderId);
            fail("Should have thrown a NoSuchElementException");
        }catch(NoSuchElementException e) {
            // Assert
            assertEquals("Order with given id does not exist", e.getMessage());
        }

        // Assert
        verify(orderRepository, never()).save(any());
    }
}
