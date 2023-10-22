package com.kursinis.ptkursinis.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("C")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Customer extends User{
    private String address;

    public Customer(String username, String password, String email, String firstName, String lastName, String phoneNumber, String address) {
        super(username, password, email, firstName, lastName, phoneNumber);
        this.address = address;
    }
}
