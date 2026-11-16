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
        checkStatusIsNotNull(status);
        this.status = status;
        this.setPrice(price);
    }

    public Order(OrderStatus status)
    {
        checkStatusIsNotNull(status);
        this.status = status;
        this.price = 1.0f;
    }

    public Order(float price)
    {
        this.status = OrderStatus.DRAFT;
        this.setPrice(price);
    }

    public Order() {
        this.status = OrderStatus.DRAFT;
        this.price = 1.0f;
    }

    public OrderStatus getStatus()
    {
        return status;
    }

    private void setStatus(OrderStatus status)
    {
        checkStatusIsNotNull(status);
        if(status == this.status) {
            throw new IllegalStateException("The order is already " + getStatusToString(status));
        }else if(status.ordinal() != this.status.ordinal() + 1) {
            throw new IllegalStateException("The order cannot be " + getStatusToString(status));
        }
        this.status = status;
    }

    private void checkStatusIsNotNull(OrderStatus status){
        if(status == null){
            throw new NullPointerException("Status cannot be null");
        }
    }

    private String getStatusToString(OrderStatus status) {
        return switch (status) {
            case OrderStatus.DRAFT -> "draft";
            case OrderStatus.PROCESSED -> "processed";
            case OrderStatus.DISPATCHED -> "dispatched";
            case OrderStatus.DELIVERED -> "delivered";
        };
    }

    public void processOrder() {
        setStatus(OrderStatus.PROCESSED);
    }

    public void dispatchOrder() {
        setStatus(OrderStatus.DISPATCHED);
    }

    public void deliverOrder() {
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
