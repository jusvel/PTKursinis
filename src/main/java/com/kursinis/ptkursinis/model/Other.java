package com.kursinis.ptkursinis.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("Other")
@Getter
@Setter
public class Other extends Product{
    public Other(String name, double price, String brand, Warehouse warehouse) {
        super(name, price, brand, warehouse);
    }
    public Other(){}
}
