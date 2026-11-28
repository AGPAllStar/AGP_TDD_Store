package org.kata.store;

import org.junit.jupiter.api.Test;
import org.kata.store.repository.ConnectionBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * Prueba de integración (TestContainers).
 *
 * A diferencia de los tests unitarios, aquí NO se usan mocks: se conectan las piezas reales
 * (servicios + repositorios) y se prueba un flujo completo. Por ejemplo:
 *
 *   dar de alta un usuario -> crear un pedido -> avanzarlo hasta DELIVERED -> comprobar su estado final
 *
 * Repositorios con PostgreSQL probados con Testcontainers
 * (@Testcontainers, @Container y PostgreSQLContainer; las dependencias ya están en el pom.xml).
 *
 * Las clases que terminan en IT las ejecuta maven-failsafe-plugin con `mvn verify`, no con `mvn test`.
 */
@Testcontainers
class OrderRegistryTCIT extends AbstractOrderRegistryITest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15");

    @Test
    void createUserAndOrderToDeliver() {
        setUp();

        super.createUserAndOrderToDeliverTest();

        shutDown();
    }

    @Override
    public void setUp() {
        postgres.start();

        connBuild = new ConnectionBuilder(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
    }

    @Override
    public void shutDown() {
        postgres.stop();
    }
}
