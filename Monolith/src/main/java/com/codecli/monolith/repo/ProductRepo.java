package com.codecli.monolith.repo;

import com.codecli.monolith.Models.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepo extends CrudRepository<Product,Long> {

    @Query("select p.iva from Product p")
    List<Float> findAllIvas();
}
