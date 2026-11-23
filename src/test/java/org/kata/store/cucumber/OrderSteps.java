package org.kata.store.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.kata.store.domain.Order;
import org.kata.store.domain.OrderStatus;
import org.kata.store.domain.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderSteps {

    private Order order;

    @Given("un usuario con id {long} llamado {string} {string}")
    public void createUser(Long id, String name, String surname) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setSurname(surname);
    }

    @Given("un pedido en estado {string}")
    public void createOrderInStatus(String status) {
        order = new Order(OrderStatus.valueOf(status));
    }

    @When("el usuario {long} crea un pedido de {float} euros")
    public void createOrderForUser(Long userId, Float price) {
        order = new Order();
        order.setUserId(userId);
        order.setPrice(price);
    }

    @When("avanzo el pedido")
    public void orderAdvances() {
        switch(order.getStatus()) {
            case DRAFT -> order.processOrder();
            case PROCESSED -> order.dispatchOrder();
            case DISPATCHED -> order.deliverOrder();
        };
    }

    @Then("el pedido está en estado {string}")
    public void orderCreatedInStatus(String status) {
        assertNotNull(order);
        assertEquals(OrderStatus.valueOf(status), order.getStatus());
    }
}
