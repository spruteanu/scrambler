package org.prismus.scrambler.beans

/**
 * @author Serge Pruteanu
 */
class School {
    int schoolId
    String name
    List<ClassRoom> rooms
    Address address

    Person principle
    List<Person> staff

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
    int classRoomId

    School parent
    int schoolId
    String roomNumber

    Person teacher
    List<Person> students
}

class Person {
    long personId

    String firstName
    String middleName
    String lastName
    String dob

    String sex
    String phone

    Address address
}

class Address {
    String number
    String street
    String city
    String state
    String postalCode
}

class Order {
    Person person
    BigDecimal total
    List<OrderItem> items = new ArrayList<OrderItem>()

    int[] arrayField
}

class Product {
    String name
    BigDecimal price
}

class OrderItem {
    int quantity
    String details
    Date orderTime
    Product product
}

class Book {
    String author
    String title
    Integer isbn
    Integer numberOfPages
    String publisher
}
