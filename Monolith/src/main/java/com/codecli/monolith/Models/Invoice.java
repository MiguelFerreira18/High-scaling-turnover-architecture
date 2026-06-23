package com.codecli.monolith.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.DatatypeConverter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Entity
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String ID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "product_id",
            joinColumns = @JoinColumn(name = "invoice_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    @NotNull
    @Column(name = "is_paid")
    private boolean isPaid;

    @NotNull
    @Column(name = "atcud", nullable = false)
    private String ATCUD; //Hash md5 do id

    @NotNull
    @Column(name = "total_after_tax", nullable = false, precision = 2)
    private float totalAfterTax;

    @NotNull
    @Column(name = "total", nullable = false, precision = 2)
    private float total;

    @NotNull
    @Column(name = "total_liquidated_iva", nullable = false, precision = 2)
    private float totalLiquidatedIva;

    public Invoice(String ID, User user, List<Product> products, boolean isPaid, String ATCUD, float totalAfterTax, float total, float totalLiquidatedIva) {
        this.ID = ID;
        this.user = user;
        this.products = products;
        this.isPaid = isPaid;
        this.ATCUD = ATCUD;
        this.totalAfterTax = totalAfterTax;
        this.total = total;
        this.totalLiquidatedIva = totalLiquidatedIva;
    }

    public Invoice(@NotNull User user, @NotNull List<Product> products, boolean isPaid) {
        this.user = user;
        this.products = products;
        this.isPaid = isPaid;

        try {
            this.ATCUD = this.createAtcud(products, user);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        try {
            this.total = this.calculateTotal(products, false);
            this.totalAfterTax = this.calculateTotal(products, true);
            this.totalLiquidatedIva = this.calculateTotalLiquidatedIva(products);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Invoice() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public String getATCUD() {
        return ATCUD;
    }

    public void setATCUD(String ATCUD) {
        this.ATCUD = ATCUD;
    }

    public float getTotalAfterTax() {
        return totalAfterTax;
    }

    public void setTotalAfterTax(float totalAfterTax) {
        this.totalAfterTax = totalAfterTax;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getTotalLiquidatedIva() {
        return totalLiquidatedIva;
    }

    public void setTotalLiquidatedIva(float totalLiquidatedIva) {
        this.totalLiquidatedIva = totalLiquidatedIva;
    }

    private String createAtcud(List<Product> products, User user) throws NoSuchAlgorithmException {
        Optional<Float> sum = products.stream()
                .map(p -> (p.getPrice() * p.getIva()) + p.getPrice())
                .reduce(Float::sum);
        if (sum.isPresent()) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            String code = sum.get() + user.getUsername() + user.getEmail();
            md.update(code.getBytes());
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest);
        } else {
            throw new RuntimeException("An error occurred while summing product values");
        }
    }

    private float calculateTotal(List<Product> products, boolean hasTax) {
        if (hasTax) {
            return products.stream()
                    .map(p -> (p.getPrice() * p.getIva()) + p.getPrice())
                    .reduce(Float::sum)
                    .orElseThrow(() -> new RuntimeException("An error occurred summing the products price with taxes"));
        } else {
            return products.stream()
                    .map(Product::getPrice)
                    .reduce(Float::sum)
                    .orElseThrow(() -> new RuntimeException("An error occurred summing the products price without taxes"));
        }
    }

    private float calculateTotalLiquidatedIva(List<Product> products) {
        return products.stream()
                .map(p -> p.getPrice() * p.getIva())
                .reduce(Float::sum)
                .orElseThrow(() -> new RuntimeException("An error occurred summing the liquidated iva"));
    }
}
