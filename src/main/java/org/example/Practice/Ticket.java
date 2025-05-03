package org.example.Practice;

public class Ticket {
    private final Integer id;
    private final String email;
    private final Double price;
    private final Integer voyageId;

    public Ticket(Integer id, String email, Double price, Integer voyageId) {
        this.id = id;
        this.email = email;
        this.price = price;
        this.voyageId = voyageId;
    }

    public Ticket(int id, double price) {
        this.id = id;
        this.email = null;
        this.price = price;
        this.voyageId = null;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getVoyageId() {
        return voyageId;
    }
}