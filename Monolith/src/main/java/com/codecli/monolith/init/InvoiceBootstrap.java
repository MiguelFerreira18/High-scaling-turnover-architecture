package com.codecli.monolith.init;

import com.codecli.monolith.Models.User;
import com.codecli.monolith.dto.SaveInvoice;
import com.codecli.monolith.repo.ProductRepo;
import com.codecli.monolith.repo.UserRepo;
import com.codecli.monolith.service.InvoiceService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Component
@Order(BootstrapOrder.INVOICES)
public class InvoiceBootstrap implements CommandLineRunner {
    private final InvoiceService invoiceService;
    private final ProductRepo productRepo;
    private final UserRepo userRepo;

    public InvoiceBootstrap(InvoiceService invoiceService, ProductRepo productRepo, UserRepo userRepo) {
        this.invoiceService = invoiceService;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        List<User> users = StreamSupport.stream(userRepo.findAll().spliterator(), false)
                .toList();
        Long numberOfProducts = productRepo.countProducts();
        createInvoices(users, numberOfProducts, 1000);
    }

    private void createInvoices(List<User> users, Long numberOfProducts, int nInvoices) {
        for (int i = 0; i < nInvoices; i++) {
            String userId = users.get(ThreadLocalRandom.current().nextInt(0, users.size())).getId();
            long[] productsIds = randomProductIds(numberOfProducts);
            SaveInvoice invoices = new SaveInvoice(userId, productsIds, true);
            invoiceService.saveInvoice(invoices);
        }
    }

    private long[] randomProductIds(Long numberOfProducts) {
        return IntStream.range(1, 100)
                .mapToLong(i -> ThreadLocalRandom.current().nextLong(1, numberOfProducts))
                .toArray();
    }

    //For simulation purposes this is fine, but when benchmarking it shouldn't be used
    private int randomNumberOfProducts() {
        return ThreadLocalRandom.current().nextInt(2, 100);
    }

}
