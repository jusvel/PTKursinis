package com.kursinis.ptkursinis.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("Smartwatch")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Smartwatch extends Product{
    int diameter;

    public Smartwatch(String name, double price, String brand, Warehouse warehouse, int diameter) {
        super(name, price, brand, warehouse);
        this.diameter = diameter;
    }
}
