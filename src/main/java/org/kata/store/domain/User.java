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

    private long id;
    private String name;
    private String surname;

    public User(long id, String name, String surname) {
        if(id <= 0){
            throw new IllegalArgumentException("Id cannot be zero or negative");
        }
        this.id = id;
        this.setName(name);
        this.setSurname(surname);
    }

    public User(String name, String surname) {
        this.setName(name);
        this.setSurname(surname);
    }

    private void setName(String name) {
        checkStringIsNotNullOrEmpty(name, "Name");
        this.name = name;
    }

    private void setSurname(String surname) {
        checkStringIsNotNullOrEmpty(surname, "Surname");
        this.surname = surname;
    }

    private void checkStringIsNotNullOrEmpty(String strToCheck, String strNameForErrMsg){
        if (strToCheck == null) {
            throw new IllegalArgumentException(strNameForErrMsg + " cannot be null");
        }else if (strToCheck.trim().isEmpty()) {
            throw new IllegalArgumentException(strNameForErrMsg + " cannot be empty");
        }
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }
}
