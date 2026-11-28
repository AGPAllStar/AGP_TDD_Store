package org.kata.store;

import org.kata.store.domain.Order;
import org.kata.store.domain.OrderStatus;
import org.kata.store.domain.User;
import org.kata.store.repository.OrderSQLRepository;
import org.kata.store.repository.OrderRepository;
import org.kata.store.repository.UserSQLRepository;
import org.kata.store.repository.UserRepository;
import org.kata.store.repository.ConnectionBuilder;
import org.kata.store.service.OrderService;
import org.kata.store.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class AbstractOrderRegistryITest {

    ConnectionBuilder connBuild;

    void createUserAndOrderToDeliverTest(){
        /*
            1- Register user
         */
        UserRepository userRepository = new UserSQLRepository(connBuild);
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
        OrderRepository orderRepository = new OrderSQLRepository(connBuild);
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

    public abstract void setUp();

    public abstract void shutDown();
}
