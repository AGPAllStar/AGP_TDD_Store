package org.kata.store;

import org.junit.jupiter.api.Test;
import org.kata.store.domain.Order;
import org.kata.store.domain.OrderStatus;
import org.kata.store.domain.User;
import org.kata.store.repository.OrderPostgreRepository;
import org.kata.store.repository.OrderRepository;
import org.kata.store.repository.UserPostgreRepository;
import org.kata.store.repository.UserRepository;
import org.kata.store.service.OrderService;
import org.kata.store.service.UserService;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * TODO: Prueba de integración (repositorio en memoria obligatorio, TestContainers opcional).
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
            new PostgreSQLContainer<>("postgres:16");

    private UserService userService;
    private OrderService orderService;

    private UserRepository userRepository;
    private OrderRepository orderRepository;

    @Test
    void createUserAndOrderToDeliver() {
        /*
            1- Register user
         */
        // Arrange
        postgres.start();

        var userRepository = new UserPostgreRepository(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword());
        userService = new UserService(userRepository);

        User user = new User();
        user.setId(1L);
        user.setName("Armando");
        user.setSurname("Guerra");

        // Act
        userService.registerUser(user);

        // Assert
        assertEquals(1, userRepository.count());
        assertTrue(userRepository.findById(user.getId()).isPresent());

        /*
            2- Register order
         */
        // Arrange
        var orderRepository = new OrderPostgreRepository(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword());
        orderService = new OrderService(orderRepository, userService);

        Order order = new Order();
        order.setId(1L);
        order.setPrice(32.0f);
        order.setUserId(user.getId());

        // Act
        orderService.createOrder(order);

        // Assert
        assertEquals(1, orderRepository.count());
        assertTrue(orderRepository.findById(order.getId()).isPresent());

        /*
            3- Process order
         */
        // Act
        orderService.processOrder(order.getId());

        // Assert
        assertEquals(1, orderRepository.count());
        assertEquals(OrderStatus.PROCESSED, orderRepository.findById(order.getId()).get().getStatus());

        /*
            4- Dispatch order
         */
        // Act
        orderService.dispatchOrder(order.getId());

        // Assert
        assertEquals(1, orderRepository.count());
        assertEquals(OrderStatus.DISPATCHED, orderRepository.findById(order.getId()).get().getStatus());

        /*
            5- Deliver order
         */
        // Act
        orderService.deliverOrder(order.getId());

        // Assert
        assertEquals(1, orderRepository.count());
        assertEquals(OrderStatus.DELIVERED, orderRepository.findById(order.getId()).get().getStatus());
    }
}
