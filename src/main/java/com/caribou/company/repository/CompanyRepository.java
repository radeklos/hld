package com.caribou.company.repository;


import com.caribou.auth.domain.UserAccount;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.CompanyEmployee;
import com.caribou.company.domain.Role;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


public interface CompanyRepository extends CrudRepository<Company, Long> {

    @Query("select e " +
            "from CompanyEmployee e " +
            "join e.member u " +
            "WHERE u.email = :email and e.company.uid = :uid")
    Optional<CompanyEmployee> findEmployeeByEmailForUid(@Param("email") String email, @Param("uid") Long uid);

    @Query("select c " +
            "from Company c " +
            "join c.employees e " +
            "join e.member u " +
            "WHERE u.email = :email")
    Optional<Company> findByEmployeeEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query(value = "insert into company_employee (company_uid, member_uid, role, created_at, updated_at) values(:#{#company.uid}, :#{#member.uid}, :#{#role.name}, now(), now())", nativeQuery = true)
    void addEmployee(@Param("company") Company company, @Param("member") UserAccount userAccount, @Param("role") Role role);

    @Query("select e from CompanyEmployee e where e.member = ?1")
    Optional<CompanyEmployee> findEmployeeByUserAccount(UserAccount user);

}
