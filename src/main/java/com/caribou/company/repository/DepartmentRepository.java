package com.caribou.company.repository;


import com.caribou.auth.domain.UserAccount;
import com.caribou.company.domain.Department;
import com.caribou.company.domain.DepartmentEmployee;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface DepartmentRepository extends CrudRepository<Department, UUID> {

    @Modifying
    @Transactional
    @Query("delete from DepartmentEmployee e where e.member = ?1")
    void removeEmployee(UserAccount userAccount);

    List<Department> findByCompanyUid(UUID uuid);

    @Deprecated
    default void addEmployee(@Param("department") Department department, @Param("member") UserAccount userAccount) {
        addEmployee(UUID.randomUUID(), department, userAccount);
    }

    @Modifying
    @Transactional
    @Query(value = "insert into department_employee (uid, department_uid, member_uid, created_at, updated_at) values(:#{#uuid}, :#{#department.uid}, :#{#member.uid}, now(), now())", nativeQuery = true)
    void addEmployee(@Param("uuid") UUID uuid, @Param("department") Department department, @Param("member") UserAccount userAccount);

    @Query("select e from DepartmentEmployee e where e.department = ?1 and e.member = ?2")
    Optional<DepartmentEmployee> getEmployee(Department department, UserAccount user);

}
