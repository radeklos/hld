package com.caribou.company.repository;


import com.caribou.company.domain.Company;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


public interface CompanyRepository extends CrudRepository<Company, Long> {

    @Query("from Company c " +
            "join c.employees e " +
            "join e.member u " +
            "WHERE u.email = :email and c.uid = :uid")
    Company findEmployeeByEmailForUid(@Param("email") String email, @Param("uid") Long uid);

}
