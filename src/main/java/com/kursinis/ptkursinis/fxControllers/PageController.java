package com.kursinis.ptkursinis.fxControllers;

import com.kursinis.ptkursinis.model.User;
import jakarta.persistence.EntityManagerFactory;

public interface PageController {
    void setData(EntityManagerFactory entityManagerFactory, User currentUser);
}
