package org.example.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Table(name = "tickets", schema = "maritime_booking")
@IdClass(TicketId.class)
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Id
    @Column(name = "voyage_id")
    private Long voyageId;

    @Id
    @Column(name = "vessel_id")
    private String vesselId;

    @Id
    @Column(name = "cabin_id")
    private Long cabinId;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "voyage_id", referencedColumnName = "id", insertable = false, updatable = false),
            @JoinColumn(name = "vessel_id", referencedColumnName = "vessel_id", insertable = false, updatable = false)
    })
    private Voyage voyage;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "cabin_id", referencedColumnName = "id", insertable = false, updatable = false),
            @JoinColumn(name = "vessel_id", referencedColumnName = "vessel_id", insertable = false, updatable = false)
    })
    private Cabin cabin;

    @ManyToOne
    @JoinColumn(name = "email", nullable = false)
    private Customer customer;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "payment_method", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "meal_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private MealType mealType;

    @Column(nullable = false)
    private Boolean insurance;

    @Column(name = "luggage_weight", nullable = false)
    private Integer luggageWeight;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    public enum PaymentMethod {
        card, cash
    }

    public enum MealType {
        no_meals, breakfast, half_board, full_board, all_inclusive, ultra_all_inclusive
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVoyageId() {
        return voyageId;
    }

    public void setVoyageId(Long voyageId) {
        this.voyageId = voyageId;
    }

    public String getVesselId() {
        return vesselId;
    }

    public void setVesselId(String vesselId) {
        this.vesselId = vesselId;
    }

    public Long getCabinId() {
        return cabinId;
    }

    public void setCabinId(Long cabinId) {
        this.cabinId = cabinId;
    }

    public Voyage getVoyage() {
        return voyage;
    }

    public void setVoyage(Voyage voyage) {
        this.voyage = voyage;
    }

    public Cabin getCabin() {
        return cabin;
    }

    public void setCabin(Cabin cabin) {
        this.cabin = cabin;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public Boolean getInsurance() {
        return insurance;
    }

    public void setInsurance(Boolean insurance) {
        this.insurance = insurance;
    }

    public Integer getLuggageWeight() {
        return luggageWeight;
    }

    public void setLuggageWeight(Integer luggageWeight) {
        this.luggageWeight = luggageWeight;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}