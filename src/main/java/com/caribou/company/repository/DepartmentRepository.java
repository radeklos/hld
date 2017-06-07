package com.caribou.company.repository;


import com.caribou.auth.domain.UserAccount;
import com.caribou.company.domain.Department;
import com.caribou.company.domain.DepartmentEmployee;
import com.caribou.company.domain.Role;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;


public interface DepartmentRepository extends CrudRepository<Department, String> {

    @Modifying
    @Transactional
    @Query("delete from DepartmentEmployee e where e.member = ?1")
    void removeEmployee(UserAccount userAccount);

    @Modifying
    @Transactional
    @Query(value = "insert into department_employee (department_uid, member_uid, role, remaining_days_off, created_at, updated_at) values(:#{#department.uid}, :#{#member.uid}, :#{#role.name}, :#{#remainingDaysOff}, now(), now())", nativeQuery = true)
    void addEmployee(@Param("department") Department department, @Param("member") UserAccount userAccount, @Param("remainingDaysOff") BigDecimal remainingDaysOff, @Param("role") Role role);

    @Modifying
    @Transactional
    @Query(value = "update DepartmentEmployee e set e.role = ?2 where e.member = ?1")
    void updateEmployee(UserAccount userAccount, Role role);

    @Query("select e from DepartmentEmployee e where e.department = ?1 and e.member = ?2")
    Optional<DepartmentEmployee> getEmployee(Department department, UserAccount user);
}
