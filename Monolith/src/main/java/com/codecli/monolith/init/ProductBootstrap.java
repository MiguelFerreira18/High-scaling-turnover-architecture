package com.codecli.monolith.init;

import com.codecli.monolith.dto.SaveProduct;
import com.codecli.monolith.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Order(BootstrapOrder.PRODUCTS)
public class ProductBootstrap implements CommandLineRunner {
    private ProductService productService;

    public ProductBootstrap(ProductService productService) {
        this.productService = productService;
    }

    public void run(String... args) throws Exception {
        createProduct(10000);
    }

    private void createProduct(int nProduct) throws NoSuchAlgorithmException {
        for (int i = 0; i < nProduct; i++) {
            float iva = ThreadLocalRandom.current().nextFloat(0.0f, 0.30f);
            float formatIva = Math.round(iva * 100.0f) / 100.0f;
            float price = ThreadLocalRandom.current().nextFloat(0.1f, 100f);
            float formatPrice = Math.round(price * 100.0f) / 100.0f;
            SaveProduct p = new SaveProduct("PROD-" + i, formatIva, formatPrice);
            if (!productService.doesProductExist(p.name())) {
                productService.saveProduct(p);
            }
        }
    }
}
