package com.codecli.monolith.Models;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long ID;
    @UuidGenerator
    @Column(name = "sku", nullable = false)
    private String sku;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "iva", precision = 3, nullable = false)
    private float iva;
    @Column(name = "price", precision = 2, nullable = false)
    private float price;

    public Product(Long ID, String sku, String name, float iva, float price) {
        this.ID = ID;
        this.sku = sku;
        this.name = name;
        this.iva = iva;
        this.price = price;
    }

    public Product(String sku, String name, float iva, float price) {
        this.sku = sku;
        this.name = name;
        this.iva = iva;
        this.price = price;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getIva() {
        return iva;
    }

    public void setIva(float iva) {
        this.iva = iva;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
