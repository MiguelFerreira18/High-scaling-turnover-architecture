package com.codecli.monolith.repo;

import com.codecli.monolith.Models.CompanyInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepo extends CrudRepository<CompanyInfo,Long> {
}
