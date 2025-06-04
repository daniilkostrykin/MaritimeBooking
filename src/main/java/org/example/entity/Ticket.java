package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tickets", schema = "maritime_booking")
@IdClass(TicketId.class)
public class Ticket {
    @Id
    @Column(name = "id")
    private Long id;

    @Id
    @Column(name = "voyage_id")
    private Long voyageId;

    @Id
    @Column(name = "vessel_id", length = 10)
    private String vesselId;

    @Id
    @Column(name = "cabin_id")
    private Long cabinId;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "payment_method", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "meal_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private MealType mealType;

    @Column(name = "insurance", nullable = false)
    private Boolean insurance;

    @Column(name = "luggage_weight", nullable = false)
    private Integer luggageWeight;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @ManyToOne
    @JoinColumn(name = "email", referencedColumnName = "email", insertable = false, updatable = false)
    private Customer customer;

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

    public enum PaymentMethod {
        card, cash
    }

    public enum MealType {
        no_meals, breakfast, half_board, full_board, all_inclusive, ultra_all_inclusive
    }
}