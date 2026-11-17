package org.kata.store.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/*
 * TODO: Tests unitarios de Order y de su flujo de estados.
 *
 * Empieza aquí: es la parte sin dependencias, la más fácil para arrancar el ciclo Red -> Green -> Refactor.
 *
 * Ideas de tests (uno por comportamiento, de uno en uno):
 *   - shouldStartInDraftStatus
 *   - shouldMoveFromDraftToProcessed
 *   - shouldNotSkipFromDraftToDispatched
 *   - shouldNotChangeStatusWhenDelivered
 *   - shouldRejectPriceLowerOrEqualThanZero  (prueba con @ParameterizedTest y @ValueSource)
 *
 * Recuerda:
 *   - Patrón AAA: // Arrange, // Act, // Assert.
 *   - Haz un commit en rojo, otro en verde y otro tras refactorizar.
 */
class OrderTest {

    // Arrange
    private Order order;

    @Test
    void shouldStartInDraftStatus() {
        // Act
        order = new Order();

        // Assert
        assertEquals(OrderStatus.DRAFT, order.getStatus());
    }

    @Test
    void shouldNotAcceptANullStatus() {
        // Assert
        assertThrows(NullPointerException.class, () -> {
            // Act
            new Order(null);
        });
    }

    @Test
    void shouldChangeFromDraftToProcessedStatus() {
        // Arrange
        order = new Order(OrderStatus.DRAFT);

        // Act
        order.processOrder();

        // Assert
        assertEquals(OrderStatus.PROCESSED, order.getStatus());
    }

    @Test
    void shouldNotChangeFromProcessedToProcessedStatus() {
        // Arrange
        order = new Order(OrderStatus.PROCESSED);

        try {
            // Act
            order.processOrder();
            fail("Should have thrown an IllegalStateException");
        }catch(IllegalStateException e) {
            // Assert
            assertEquals("The order is already processed", e.getMessage());
        }

        // Assert
        assertEquals(OrderStatus.PROCESSED, order.getStatus());
    }

    @Test
    void shouldChangeFromProcessedToDispatchedStatus() {
        // Arrange
        order = new Order(OrderStatus.PROCESSED);

        // Act
        order.dispatchOrder();

        // Assert
        assertEquals(OrderStatus.DISPATCHED, order.getStatus());
    }

    @Test
    void shouldNotChangeFromDraftToDispatchedStatus() {
        // Arrange
        order = new Order(OrderStatus.DRAFT);

        try {
            // Act
            order.dispatchOrder();
            fail("Should have thrown an IllegalStateException");
        }catch(IllegalStateException e) {
            // Assert
            assertEquals("The order cannot be dispatched", e.getMessage());
        }

        // Assert
        assertEquals(OrderStatus.DRAFT, order.getStatus());
    }

    @Test
    void shouldNotChangeFromDispatchedToProcessedStatus() {
        // Arrange
        order = new Order(OrderStatus.DISPATCHED);

        try {
            // Act
            order.processOrder();
            fail("Should have thrown an IllegalStateException");
        }catch(IllegalStateException e) {
            // Assert
            assertEquals("The order cannot be processed", e.getMessage());
        }

        // Assert
        assertEquals(OrderStatus.DISPATCHED, order.getStatus());
    }

    @Test
    void shouldNotChangeFromDispatchedToDispatchedStatus() {
        // Arrange
        order = new Order(OrderStatus.DISPATCHED);

        try {
            // Act
            order.dispatchOrder();
            fail("Should have thrown an IllegalStateException");
        }catch(IllegalStateException e) {
            // Assert
            assertEquals("The order is already dispatched", e.getMessage());
        }

        // Assert
        assertEquals(OrderStatus.DISPATCHED, order.getStatus());
    }

    @Test
    void shouldChangeFromDispatchedToDeliveredStatus() {
        // Arrange
        order = new Order(OrderStatus.DISPATCHED);

        // Act
        order.deliverOrder();

        // Assert
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    void shouldNotChangeFromDraftToDeliveredStatus() {
        // Arrange
        order = new Order(OrderStatus.DRAFT);

        try {
            // Act
            order.deliverOrder();
            fail("Should have thrown an IllegalStateException");
        }catch(IllegalStateException e) {
            // Assert
            assertEquals("The order cannot be delivered", e.getMessage());
        }

        // Assert
        assertEquals(OrderStatus.DRAFT, order.getStatus());
    }

    @Test
    void shouldNotChangeFromProcessedToDeliveredStatus() {
        // Arrange
        order = new Order(OrderStatus.PROCESSED);

        try {
            // Act
            order.deliverOrder();
            fail("Should have thrown an IllegalStateException");
        }catch(IllegalStateException e) {
            // Assert
            assertEquals("The order cannot be delivered", e.getMessage());
        }

        // Assert
        assertEquals(OrderStatus.PROCESSED, order.getStatus());
    }

    @Test
    void shouldNotChangeFromDeliveredToDeliveredStatus() {
        // Arrange
        order = new Order(OrderStatus.DELIVERED);

        try {
            // Act
            order.deliverOrder();
            fail("Should have thrown an IllegalStateException");
        }catch(IllegalStateException e) {
            // Assert
            assertEquals("The order is already delivered", e.getMessage());
        }

        // Assert
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    void shouldNotChangeFromDeliveredToProcessedStatus() {
        // Arrange
        order = new Order(OrderStatus.DELIVERED);

        try {
            // Act
            order.processOrder();
            fail("Should have thrown an IllegalStateException");
        }catch(IllegalStateException e) {
            // Assert
            assertEquals("The order cannot be processed", e.getMessage());
        }

        // Assert
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    void shouldNotChangeFromDeliveredToDispatchedStatus() {
        // Arrange
        order = new Order(OrderStatus.DELIVERED);

        try {
            // Act
            order.dispatchOrder();
            fail("Should have thrown an IllegalStateException");
        }catch(IllegalStateException e) {
            // Assert
            assertEquals("The order cannot be dispatched", e.getMessage());
        }

        // Assert
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @ParameterizedTest
    @ValueSource(floats = {1.0f, 3.5f, 5.99f})
    void shouldAcceptPriceHigherThanZero(float newPrice) {
        // Act
        order = new Order(newPrice);

        // Assert
        assertEquals(newPrice, order.getPrice());
    }

    @ParameterizedTest
    @ValueSource(floats = {0.0f, -1.5f, -5.99f})
    void shouldRejectPriceLowerOrEqualThanZero(float newPrice) {
        try {
            // Act
            order = new Order(newPrice);
        }catch(IllegalArgumentException e) {
            // Assert
            assertEquals("Price must be greater than zero", e.getMessage());
        }

        // Assert
        assertNull(order);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 3L, Long.MAX_VALUE})
    void shouldAcceptUserIdHigherThanZero(long newUserId) {
        // Arrange
        Order order = new Order();

        // Act
        order.setUserId(newUserId);

        // Assert
        assertEquals(newUserId, order.getUserId());
    }
}
