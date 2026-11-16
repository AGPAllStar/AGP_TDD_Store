package org.kata.store.domain;

/*
 * TODO: Usuario de la tienda.
 *
 * Datos: id, nombre y apellidos.
 *
 * Requisitos (escribe primero el test de cada uno):
 *   - El nombre y los apellidos son obligatorios: ni nulos ni vacíos.
 *   - Si no se cumple, se lanza una excepción con un mensaje claro.
 */
public class User {

    private String name;
    private String surname;

    public User(String name, String surname) {
        this.setName(name);
        this.setSurname(surname);
    }

    private void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }else if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }

    private void setSurname(String surname) {
        if (surname == null) {
            throw new IllegalArgumentException("Surname cannot be null");
        }else if (surname.trim().isEmpty()) {
            throw new IllegalArgumentException("Surname cannot be empty");
        }
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }
}
