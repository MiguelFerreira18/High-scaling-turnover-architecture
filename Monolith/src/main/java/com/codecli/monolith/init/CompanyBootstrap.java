package com.codecli.monolith.init;

import com.codecli.monolith.dto.SaveCompany;
import com.codecli.monolith.service.CompanyService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(BootstrapOrder.COMPANY)
public class CompanyBootstrap implements CommandLineRunner {
    private final CompanyService companyService;

    public CompanyBootstrap(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Override
    public void run(String... args) throws Exception {
        SaveCompany c = new SaveCompany(824505212,"MicroS","MiS Lda.","AmazingMicroSoftware","Buckingham Palace Road");
        if (!companyService.doesCompanyExist(c.name())){
            companyService.saveCompany(c);
        }
    }
}
