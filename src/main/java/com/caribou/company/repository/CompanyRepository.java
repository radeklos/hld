package com.caribou.company.repository;


import com.caribou.company.domain.Company;
import org.springframework.data.repository.CrudRepository;


public interface CompanyRepository extends CrudRepository<Company, Long> {

}
