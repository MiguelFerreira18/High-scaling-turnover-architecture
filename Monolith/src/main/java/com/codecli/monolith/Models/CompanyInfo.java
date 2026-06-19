package com.codecli.monolith.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class CompanyInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int ID;

    @NotNull
    @Column(name = "nif", nullable = false)
    private int nif;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "firm", nullable = false)
    private String firm;

    @NotNull
    @Column(name = "company_name", nullable = false)
    private String companyName; //wtf

    @NotNull
    @Column(name = "tax_address", nullable = false)
    private String taxAddress;

    public CompanyInfo(int ID, int nif, String name, String firm, String companyName, String taxAddress) {
        this.ID = ID;
        this.nif = isNifValid(nif);
        this.name = name;
        this.firm = firm;
        this.companyName = companyName;
        this.taxAddress = taxAddress;
    }

    public CompanyInfo(int nif, String name, String firm, String companyName, String taxAddress) {
        this.nif = isNifValid(nif);
        this.name = name;
        this.firm = firm;
        this.companyName = companyName;
        this.taxAddress = taxAddress;
    }

    public CompanyInfo(){

    }

    public int getID() {
        return ID;
    }

    public CompanyInfo setID(int ID) {
        this.ID = ID;
        return this;
    }

    public int getNif() {
        return nif;
    }

    public CompanyInfo setNif(int nif) {
        this.nif = nif;
        return this;
    }

    public String getName() {
        return name;
    }

    public CompanyInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getFirm() {
        return firm;
    }

    public CompanyInfo setFirm(String firm) {
        this.firm = firm;
        return this;
    }

    public String getCompanyName() {
        return companyName;
    }

    public CompanyInfo setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public String getTaxAddress() {
        return taxAddress;
    }

    public CompanyInfo setTaxAddress(String taxAddress) {
        this.taxAddress = taxAddress;
        return this;
    }

    private int isNifValid(int nif) {
        String sNif = String.valueOf(nif);
        if (sNif.length() != 9) {
            throw new RuntimeException("Invalid nif: Isn't of correct size");
        }

        int firstDigit = sNif.charAt(0) - '0';
        if (firstDigit < 1 || firstDigit > 7) {
            throw new RuntimeException("Invalid nif: Doesn't belong to any group");
        }

        int sum = 0;
        for (int i = 0; i < sNif.length() - 1; i++) {
            int val = Integer.parseInt(sNif.substring(i, i + 1));
            sum += val * (9 - i);
        }

        int remainder = sum % 11;
        int checkDigit = (remainder == 0 || remainder == 1) ? 0 : 11 - remainder;
        if (checkDigit == Integer.parseInt(sNif.substring(8, 9))) {
            return nif;
        } else {
            throw new RuntimeException("Invalid nif: Failed checksum");
        }
    }
}
