package com.kursinis.ptkursinis.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    private String name;
    private String address;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    List<Product> productsInWarehouse;

    public Warehouse(String name, String address) {
        this.name = name;
        this.address = address;
    }
}
