package com.caribou.company.repository;


import com.caribou.auth.domain.UserAccount;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Role;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface CompanyRepository extends CrudRepository<Company, Long> {

    @Query("from Company c " +
            "join c.employees e " +
            "join e.member u " +
            "WHERE u.email = :email and c.uid = :uid")
    Company findEmployeeByEmailForUid(@Param("email") String email, @Param("uid") Long uid);

    @Query("from Company c " +
            "join c.employees e " +
            "join e.member u " +
            "WHERE u.email = :email")
    Company findByEmployeeEmail(@Param("email") String email);


    @Modifying
    @Transactional
    @Query(value = "insert into company_employee (company_uid, member_uid, role, created_at, updated_at) values(:#{#company.uid}, :#{#member.uid}, :#{#role.name}, now(), now())", nativeQuery = true)
    void addEmployee(@Param("company") Company company, @Param("member") UserAccount userAccount, @Param("role") Role role);

}
