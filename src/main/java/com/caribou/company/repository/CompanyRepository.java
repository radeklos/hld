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

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface CompanyRepository extends CrudRepository<Company, UUID> {

    @Query("select e " +
            "from CompanyEmployee e " +
            "join e.member u " +
            "WHERE u.email = :email and e.company.uid = :uid")
    Optional<CompanyEmployee> findEmployeeByEmailForUid(@Param("email") String email, @Param("uid") UUID uid);

    @Query("select e " +
            "from CompanyEmployee e " +
            "join e.member u " +
            "WHERE e.company.uid = :uid")
    List<CompanyEmployee> findEmployeesByCompanyUid(@Param("uid") UUID uid);

    @Query("select e " +
            "from CompanyEmployee e " +
            "join e.member u " +
            "WHERE u.email = :email")
    Optional<CompanyEmployee> findEmployeeByEmail(@Param("email") String email);

    @Query("select e " +
            "from Company c " +
            "join c.employees e " +
            "join e.member u " +
            "WHERE u.uid = :uid")
    Optional<CompanyEmployee> findByEmployeeByUid(@Param("uid") UUID uid);

    @Query("select c " +
            "from Company c " +
            "join c.employees e " +
            "join e.member u " +
            "WHERE u.email = :email")
    Optional<Company> findByEmployeeEmail(@Param("email") String email);

    default void addEmployee(@Param("company") Company company, @Param("member") UserAccount userAccount, @Param("role") Role role) {
        addEmployee(UUID.randomUUID(), company, userAccount, role);
    }

    @Modifying
    @Transactional
    @Query(value = "insert into company_employee (uid, company_uid, member_uid, role, created_at, updated_at) values(:#{#uuid}, :#{#company.uid}, :#{#member.uid}, :#{#role.name}, now(), now())", nativeQuery = true)
    void addEmployee(@Param("uuid") UUID uuid, @Param("company") Company company, @Param("member") UserAccount userAccount, @Param("role") Role role);

    @Query("select e from CompanyEmployee e where e.member = ?1")
    Optional<CompanyEmployee> findEmployeeByUserAccount(UserAccount user);

}
