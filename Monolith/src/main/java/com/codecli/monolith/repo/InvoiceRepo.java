package com.codecli.monolith.repo;

import com.codecli.monolith.Models.Invoice;
import com.codecli.monolith.Models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface InvoiceRepo extends CrudRepository<Invoice, String> {
    @Query("select distinct i.user FROM Invoice i")
    List<User> findAllUsersWithAPurchase();
}
