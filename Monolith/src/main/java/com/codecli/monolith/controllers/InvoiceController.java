package com.codecli.monolith.controllers;

import com.codecli.monolith.Models.Invoice;
import com.codecli.monolith.dto.SaveInvoice;
import com.codecli.monolith.service.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("invoices")
public class InvoiceController {
    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public ResponseEntity<Iterable<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @PostMapping
    public ResponseEntity<Invoice> saveInvoice(@RequestBody SaveInvoice invoice) {
        try {
            return ResponseEntity.ok(invoiceService.saveInvoice(invoice));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
