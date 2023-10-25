package com.kursinis.ptkursinis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.io.Serializable;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String name;
    double price;
    String brand;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    Warehouse warehouse;
    @Column(name = "DTYPE", insertable = false, updatable = false)
    String type;


    public Product(String name, double price, String brand, Warehouse warehouse) {
        this.name = name;
        this.price = price;
        this.brand = brand;
        this.warehouse = warehouse;
    }
}
