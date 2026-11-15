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

    @Test
    void shouldStartInDraftStatus() {
        // Arrange
        Order order;

        // Act
        order = new Order();

        // Assert
        assertEquals(OrderStatus.DRAFT, order.getStatus());
    }

    @ParameterizedTest
    @ValueSource(floats = {0.0f, -1.0f})
    void shouldRejectPriceLowerOrEqualThanZero(float newPrice) {
        // Arrange
        Order order;

        // Act
        order = new Order();
        order.setPrice(newPrice);

        // Assert
        assertThrows(IllegalArgumentException.class, order.setPrice(newPrice));
        assertTrue(order.price > 0);
    }
}
