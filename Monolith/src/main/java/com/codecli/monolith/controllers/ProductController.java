package com.codecli.monolith.controllers;

import com.codecli.monolith.Models.Product;
import com.codecli.monolith.dto.SaveProduct;
import com.codecli.monolith.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Iterable<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody SaveProduct product) {
        try {
            return ResponseEntity.ok(productService.saveProduct(product));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

