package org.kata.store;

import org.junit.jupiter.api.Test;
import org.kata.store.repository.ConnectionBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/*
 * Prueba de integración (repositorio en memoria obligatorio).
 *
 * A diferencia de los tests unitarios, aquí NO se usan mocks: se conectan las piezas reales
 * (servicios + repositorios) y se prueba un flujo completo. Por ejemplo:
 *
 *   dar de alta un usuario -> crear un pedido -> avanzarlo hasta DELIVERED -> comprobar su estado final
 *
 * Repositorios en memoria (lo mínimo).
 *
 * Las clases que terminan en IT las ejecuta maven-failsafe-plugin con `mvn verify`, no con `mvn test`.
 */
class OrderRegistrySQLiteIT extends AbstractOrderRegistryITest{

    @Test
    void createUserAndOrderToDeliver() {
        setUp();

        super.createUserAndOrderToDeliverTest();

        shutDown();
    }

    @Override
    public void setUp() {
        tryToDeleteSQLiteDB();

        connBuild = new ConnectionBuilder(
                "jdbc:sqlite:target/test-database.db"
        );
    }

    @Override
    public void shutDown() {
        tryToDeleteSQLiteDB();
    }

    private void tryToDeleteSQLiteDB() {
        try {
            Files.deleteIfExists(Path.of("target/test-database.db"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to clean up test database", e);
        }
    }
}
