package org.kata.store.service;

import org.kata.store.domain.Order;
import org.kata.store.repository.OrderRepository;
import org.kata.store.service.exceptions.RepeatedIdException;

import java.lang.reflect.InvocationTargetException;

/*
 * Registro de pedidos.
 *
 * Requisitos (escribe primero el test de cada uno):
 *   - Crear un pedido para un usuario: queda en estado DRAFT.
 *   - No se puede crear un pedido para un usuario que no existe: se lanza una excepción.
 *   - No se puede crear un pedido con precio menor o igual que 0: se lanza una excepción.
 *   - Avanzar un pedido al siguiente estado (DRAFT -> PROCESSED -> DISPATCHED -> DELIVERED).
 *   - No se puede saltar un estado, retroceder ni cambiar un pedido entregado: se lanza una excepción.
 *
 * Pista: recibe UserRepository y OrderRepository por constructor.
 *   - Stub: when(userRepository.existsById(...)).thenReturn(false) para probar el usuario inexistente.
 *   - Mock: verify(orderRepository).save(...) para comprobar que el pedido se guarda.
 */
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;

    public OrderService(OrderRepository orderRepository, UserService userService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
    }

    public Order createOrder(Order order) {
        if(userService.findUserById(order.getUserId()).isEmpty()){
            throw new RepeatedIdException("It doesn't exist user with given id");
        }
        return orderRepository.save(order);
    }

    public Order processOrder(long orderId) {
        Order order = orderRepository.findById(orderId).get();
        order.processOrder();
        return orderRepository.save(order);
    }

    public Order dispatchOrder(long orderId) {
        Order order = orderRepository.findById(orderId).get();
        order.dispatchOrder();
        return orderRepository.save(order);
    }

    public Order deliverOrder(long orderId) {
        Order order = orderRepository.findById(orderId).get();
        order.deliverOrder();
        return orderRepository.save(order);
    }
}
