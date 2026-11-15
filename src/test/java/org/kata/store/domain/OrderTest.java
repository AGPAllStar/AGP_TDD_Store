package org.kata.store.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    @ValueSource(floats = {1.0f, 3.5f, 5.99f})
    void shouldAcceptPriceHigherThanZero(float newPrice) {
        // Arrange
        Order order;

        // Act
        order = new Order();
        order.setPrice(newPrice);

        // Assert
        assertEquals(newPrice, order.getPrice());
    }
}
