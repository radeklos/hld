package com.caribou.company.repository;


import com.caribou.company.domain.Department;
import org.springframework.data.repository.CrudRepository;


public interface DepartmentRepository extends CrudRepository<Department, Long> {

}
