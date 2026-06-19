package com.codecli.monolith.service;

import com.codecli.monolith.Models.Invoice;
import com.codecli.monolith.Models.Product;
import com.codecli.monolith.Models.User;
import com.codecli.monolith.dto.SaveInvoice;
import com.codecli.monolith.repo.InvoiceRepo;
import com.codecli.monolith.repo.ProductRepo;
import com.codecli.monolith.repo.UserRepo;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class InvoiceService {
    private final UserRepo userRepo;
    private final ProductRepo productRepo;
    private final InvoiceRepo invoiceRepo;

    public InvoiceService(UserRepo userRepo, ProductRepo productRepo, InvoiceRepo invoiceRepo) {
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.invoiceRepo = invoiceRepo;
    }

    public Iterable<Invoice> getAllInvoices(){
        return invoiceRepo.findAll();
    }

    public Invoice saveInvoice(SaveInvoice saveInvoice) {

        Optional<User> user = userRepo.findById(saveInvoice.userId());

        List<Product> products = StreamSupport.stream(Arrays.stream(saveInvoice.productIds()).spliterator(), false)
                .map(productRepo::findById)
                .flatMap(Optional::stream)
                .toList();

        //For the sake of simplicity, lets assume that whenever an invoice comes, it means its paid.
        Invoice invoice = new Invoice(user.get(), products, true);
        return this.invoiceRepo.save(invoice);
    }
}
