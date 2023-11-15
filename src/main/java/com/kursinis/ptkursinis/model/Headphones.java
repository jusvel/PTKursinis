package com.kursinis.ptkursinis.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("Headphones")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Headphones extends Product{
    private String connectionType;

    public Headphones(String name, double price, String brand, Warehouse warehouse, String connectionType) {
        super(name, price, brand, warehouse);
        this.connectionType = connectionType;
    }
}
