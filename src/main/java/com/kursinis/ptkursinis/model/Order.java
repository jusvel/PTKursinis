package com.kursinis.ptkursinis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String products;
    private String deliveryAddress;
    private double totalPrice;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private String responsibleEmployee;
    private LocalDate dateCreated;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    public Order(String products, String deliveryAddress, double totalPrice, OrderStatus status, PaymentStatus paymentStatus, String responsibleEmployee, LocalDate dateCreated, User user) {
        this.products = products;
        this.deliveryAddress = deliveryAddress;
        this.totalPrice = totalPrice;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.responsibleEmployee = responsibleEmployee;
        this.dateCreated = dateCreated;
        this.user = user;
    }
}
