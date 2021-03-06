package com.springboot.restdemo.service;

import java.util.List;

import com.springboot.restdemo.entity.Employee;

public interface EmployeeService {

    public List<Employee> findAll();

    public Employee findById(int theId);

    public void save(Employee employee);

    public void deleteById(int theId);
}
