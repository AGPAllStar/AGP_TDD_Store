package org.kata.store;

import org.junit.jupiter.api.Test;
import org.kata.store.domain.Order;
import org.kata.store.domain.OrderStatus;
import org.kata.store.domain.User;
import org.kata.store.repository.OrderPostgreRepository;
import org.kata.store.repository.OrderRepository;
import org.kata.store.repository.UserPostgreRepository;
import org.kata.store.repository.UserRepository;
import org.kata.store.service.ConnectionBuilder;
import org.kata.store.service.OrderService;
import org.kata.store.service.UserService;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * Prueba de integración (repositorio en memoria obligatorio, TestContainers opcional).
 *
 * A diferencia de los tests unitarios, aquí NO se usan mocks: se conectan las piezas reales
 * (servicios + repositorios) y se prueba un flujo completo. Por ejemplo:
 *
 *   dar de alta un usuario -> crear un pedido -> avanzarlo hasta DELIVERED -> comprobar su estado final
 *
 * Opciones:
 *   - Repositorios en memoria (lo mínimo).
 *   - Extra: repositorios con PostgreSQL probados con Testcontainers
 *     (@Testcontainers, @Container y PostgreSQLContainer; las dependencias ya están en el pom.xml).
 *
 * Las clases que terminan en IT las ejecuta maven-failsafe-plugin con `mvn verify`, no con `mvn test`.
 */
@Testcontainers
class OrderRegistryIT {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15");

    @Test
    void createUserAndOrderToDeliver() {
        ConnectionBuilder connBuild = new ConnectionBuilder(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );

        /*
            1- Register user
         */
        // Arrange
        postgres.start();

        UserRepository userRepository = new UserPostgreRepository(connBuild);
        UserService userService = new UserService(userRepository);

        User user = new User();
        user.setId(1L);
        user.setName("Armando");
        user.setSurname("Guerra");

        // Act
        userService.registerUser(user);

        // Assert
        assertEquals(1, userRepository.count());
        Optional<User> registeredUser = userRepository.findById(user.getId());
        assertTrue(registeredUser.isPresent());
        assertEquals(user.getId(), registeredUser.get().getId());
        assertEquals(user.getName(), registeredUser.get().getName());
        assertEquals(user.getSurname(), registeredUser.get().getSurname());

        /*
            2- Register order
         */
        // Arrange
        OrderRepository orderRepository = new OrderPostgreRepository(connBuild);
        OrderService orderService = new OrderService(orderRepository, userService);

        Order order = new Order();
        order.setId(1L);
        order.setPrice(32.0f);
        order.setUserId(user.getId());

        // Act
        orderService.createOrder(order);

        // Assert
        assertEquals(1, orderRepository.count());
        Optional<Order> createdOrder = orderRepository.findById(order.getId());
        assertTrue(createdOrder.isPresent());
        assertEquals(order.getId(), createdOrder.get().getId());
        assertEquals(order.getPrice(), createdOrder.get().getPrice());
        assertEquals(order.getStatus(), createdOrder.get().getStatus());
        assertEquals(order.getUserId(), createdOrder.get().getUserId());

        /*
            3- Process order
         */
        // Act
        orderService.processOrder(order.getId());

        // Assert
        assertEquals(1, orderRepository.count());
        Optional<Order> updatedOrder = orderRepository.findById(order.getId());
        assertTrue(updatedOrder.isPresent());
        assertEquals(OrderStatus.PROCESSED, updatedOrder.get().getStatus());

        /*
            4- Dispatch order
         */
        // Act
        orderService.dispatchOrder(order.getId());

        // Assert
        assertEquals(1, orderRepository.count());
        updatedOrder = orderRepository.findById(order.getId());
        assertTrue(updatedOrder.isPresent());
        assertEquals(OrderStatus.DISPATCHED, updatedOrder.get().getStatus());

        /*
            5- Deliver order
         */
        // Act
        orderService.deliverOrder(order.getId());

        // Assert
        assertEquals(1, orderRepository.count());
        updatedOrder = orderRepository.findById(order.getId());
        assertTrue(updatedOrder.isPresent());
        assertEquals(OrderStatus.DELIVERED, updatedOrder.get().getStatus());
    }
}
