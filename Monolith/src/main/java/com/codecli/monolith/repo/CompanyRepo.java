package com.codecli.monolith.repo;

import com.codecli.monolith.Models.CompanyInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepo extends CrudRepository<CompanyInfo,Long> {

    @Query("select c from CompanyInfo c where c.name = ?1")
    public CompanyInfo findCompanyByName(String name);
}
