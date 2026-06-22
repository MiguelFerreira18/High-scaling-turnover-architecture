package com.codecli.monolith.service;

import com.codecli.monolith.Models.Product;
import com.codecli.monolith.dto.SaveProduct;
import com.codecli.monolith.repo.ProductRepo;
import jakarta.xml.bind.DatatypeConverter;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class ProductService {
    private final ProductRepo productRepo;

    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public Product saveProduct(SaveProduct product) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        String sku = DatatypeConverter.printHexBinary(md.digest((product.name() + product.price()).getBytes()));
        Product p = new Product(sku, product.name(), product.iva(), product.price());
        return this.productRepo.save(p);
    }

    public List<Product> getAllProducts() {
        return StreamSupport.stream(this.productRepo.findAll().spliterator(), false).toList();
    }

    public Boolean doesProductExist(String product) {
        if (productRepo.findProductByName(product) != null) {
            return true;
        } else {
            return false;
        }
    }
}
