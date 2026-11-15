package org.kata.store.domain;

/*
 * TODO: Pedido de la tienda.
 *
 * Datos: id de pedido, id de usuario, estado (OrderStatus) y precio.
 *
 * Requisitos (escribe primero el test de cada uno):
 *   - Un pedido nuevo empieza en estado DRAFT.
 *   - El precio tiene que ser mayor que 0.
 *   - Cambia de estado siguiendo el flujo descrito en OrderStatus.
 *
 * Pista: para el precio, piensa en los casos límite (0, negativo, decimales).
 * Un @ParameterizedTest te ahorra repetir tests casi iguales.
 */
public class Order {
    private OrderStatus status;
    private float price;

    public Order(float price)
    {
        this.status = OrderStatus.DRAFT;
        this.setPrice(price);
    }

    public OrderStatus getStatus()
    {
        return status;
    }

    private void setStatus(OrderStatus status)
    {
        this.status = status;
    }

    public void processOrder() {
        if (status == OrderStatus.PROCESSED) {
            throw new IllegalStateException("Cannot go from Processed to Processed");
        }
        setStatus(OrderStatus.PROCESSED);
    }

    public void setPrice(float price) {
        if(price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        this.price = price;
    }

    public float getPrice() {
        return price;
    }
}
