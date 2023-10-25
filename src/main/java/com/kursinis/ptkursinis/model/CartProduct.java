package com.kursinis.ptkursinis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CartProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    int quantity = 1;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    Cart cart;

    public CartProduct(Product product, int quantity, Cart cart) {
        this.product = product;
        this.quantity = quantity;
        this.cart = cart;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CartProduct{");
        sb.append("id=").append(id);
        sb.append(", product=").append(product);
        sb.append(", quantity=").append(quantity);
        sb.append(", cart=").append(cart);
        sb.append('}');
        return sb.toString();
    }
}
