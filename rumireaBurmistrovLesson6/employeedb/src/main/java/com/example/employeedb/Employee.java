package com.example.employeedb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "employee_db")
public class Employee {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String profession;
    public int stage;

    public Employee(String name, String profession, int stage) {
        this.name = name;
        this.profession = profession;
        this.stage = stage;
    }
}
