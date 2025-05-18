package com.example.employeedb;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AppDatabase db;
    private EmployeeDAO employeeDAO;
    private ListView listViewEmployees;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> employeeStrings;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewEmployees = findViewById(R.id.list_view);

        db = App.getInstance().getDatabase();
        employeeDAO = db.employeeDAO();

        Employee employee = new Employee("Сотрудник", "Босс", 100);
        employeeDAO.insert(employee);

        List<Employee> employees = employeeDAO.getAll();
        Toast.makeText(this, "Сохранено сотрудников: " + employees.size(), Toast.LENGTH_SHORT).show();

        employeeStrings = new ArrayList<>();
        for (Employee e : employees) {
            String s = e.name + " - " + e.profession + ", стаж: " + e.stage;
            employeeStrings.add(s);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, employeeStrings);
        listViewEmployees.setAdapter(adapter);
    }
}
