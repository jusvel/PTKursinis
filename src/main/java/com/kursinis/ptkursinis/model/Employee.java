package com.kursinis.ptkursinis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("E")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Employee extends User{
    @Column(name = "employee_id")
    Integer employeeId;
    @Column(name = "employment_date")
    LocalDate employmentDate;
    @Column(name = "is_admin")
    Boolean isAdmin;

    public Employee(String username, String password, String email, String firstName, String lastName, String phoneNumber, Integer employeeId, LocalDate employmentDate, Boolean isAdmin) {
        super(username, password, email, firstName, lastName, phoneNumber);
        this.employeeId = employeeId;
        this.employmentDate = employmentDate;
        this.isAdmin = isAdmin;
    }
}
