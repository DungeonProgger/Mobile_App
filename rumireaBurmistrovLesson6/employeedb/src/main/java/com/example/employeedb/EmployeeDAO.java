package com.example.employeedb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface EmployeeDAO {
    @Query("SELECT * FROM employee_db")
    List<Employee> getAll();

    @Query("SELECT * FROM employee_db WHERE id = :id")
    Employee getById(int id);

    @Insert
    void insert(Employee employee);

    @Update
    void update(Employee employee);

    @Delete
    void delete(Employee employee);
}