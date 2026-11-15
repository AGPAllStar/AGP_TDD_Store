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

    public Order(OrderStatus status, float price)
    {
        this.setStatus(status);
        this.setPrice(price);
    }

    public Order(float price)
    {
        this(OrderStatus.DRAFT, price);
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
            throw new IllegalStateException("The order is already processed");
        }else if (status == OrderStatus.DISPATCHED) {
            throw new IllegalStateException("The order cannot be processed");
        }else if (status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("The order cannot be processed");
        }
        setStatus(OrderStatus.PROCESSED);
    }

    public void dispatchOrder() {
        if (status == OrderStatus.DRAFT) {
            throw new IllegalStateException("The order cannot be dispatched");
        }else if(status == OrderStatus.DISPATCHED) {
            throw new IllegalStateException("The order is already dispatched");
        }
        setStatus(OrderStatus.DISPATCHED);
    }

    public void deliverOrder() {
        if (status == OrderStatus.DRAFT || status == OrderStatus.PROCESSED) {
            throw new IllegalStateException("The order cannot be delivered");
        }else if (status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("The order is already delivered");
        }
        setStatus(OrderStatus.DELIVERED);
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
