package org.prismus.scrambler.beans

/**
 * todo: add description
 *
 * @author Serge Pruteanu
 */
class School {
    int schoolId
    String name
    List<ClassRoom> rooms

    School() {
    }

    School(int schoolId) {
        this.schoolId = schoolId
    }

    School(int schoolId, String name) {
        this.schoolId = schoolId
        this.name = name
    }
}

class ClassRoom {
    School parent
    int schoolId
    String roomNumber
    int parentId
}

class Order {
    BigDecimal total
    List<OrderItem> items = new ArrayList<OrderItem>()
    Person person

    int[] arrayField
}

class Product {
    String name
    BigDecimal price
}

class OrderItem {
    int quantity
    String details = "no name"
    Date orderTime
    Product product
}

class Person {
    String firstName
    String lastName
    int age
    char sex
    String phone

    Address address
}

class Address {
    String number
    String street
    String postalCode
    String city
    String room
}

class Book {
    String author
    String title
    Integer isbn
    Integer numberOfPages
    String publisher
}

class Employee {
    String name
    int age
    String designation
    double salary
}

