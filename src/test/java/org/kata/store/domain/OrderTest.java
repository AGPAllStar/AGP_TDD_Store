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
        order = new Order(1.0f);

        // Assert
        assertEquals(OrderStatus.DRAFT, order.getStatus());
    }

    @Test
    void shouldChangeFromDraftToProcessedStatus() {
        // Act
        order = new Order(1.0f);
        order.setProcessedStatus();

        // Assert
        assertEquals(OrderStatus.PROCESSED, order.getStatus());
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
}
