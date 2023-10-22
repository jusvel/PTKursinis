package com.kursinis.ptkursinis.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("Phone")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Phone extends Product{
    String os;
    double inches;
    String resolution;
    int battery;
    int ram;
    int weight;
    int storage;

    @Override
    public String toString() {
        return "Phone{" +
                "brand='" + brand + '\'' +
                ", os='" + os + '\'' +
                ", inches="+ inches +
                ", resolution='" + resolution + '\'' +
                ", battery=" + battery +
                ", ram=" + ram +
                ", weight=" + weight +
                ", storage=" + storage +
                '}'+'\n';
    }

    public Phone(String name, double price, String brand, Warehouse warehouse, String os, double inches, String resolution, int battery, int ram, int weight, int storage) {
        super(name, price, brand, warehouse);
        this.os = os;
        this.inches = inches;
        this.resolution = resolution;
        this.battery = battery;
        this.ram = ram;
        this.weight = weight;
        this.storage = storage;
    }
}
