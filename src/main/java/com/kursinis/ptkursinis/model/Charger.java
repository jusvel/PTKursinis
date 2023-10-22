package com.kursinis.ptkursinis.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@DiscriminatorValue("Charger")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Charger extends Product{
    int length;

    public Charger(String name, double price, String brand, Warehouse warehouse, int length) {
        super(name, price, brand, warehouse);
        this.length = length;
    }
}
