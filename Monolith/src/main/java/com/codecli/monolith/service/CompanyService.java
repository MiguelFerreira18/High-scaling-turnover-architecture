package com.codecli.monolith.service;

import com.codecli.monolith.Models.CompanyInfo;
import com.codecli.monolith.dto.SaveCompany;
import com.codecli.monolith.repo.CompanyRepo;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {
    private final CompanyRepo companyRepo;

    public CompanyService(CompanyRepo companyRepo) {
        this.companyRepo = companyRepo;
    }

    public CompanyInfo saveCompany(SaveCompany saveCompany) {
        CompanyInfo companyInfo = new CompanyInfo();
        companyInfo.setNif(saveCompany.nif())
                .setName(saveCompany.name())
                .setFirm(saveCompany.firm())
                .setCompanyName(saveCompany.companyName())
                .setTaxAddress(saveCompany.taxAddress());

        return companyRepo.save(companyInfo);
    }
}
