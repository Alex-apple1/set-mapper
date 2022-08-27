package com.epam.rd.autocode;


import com.epam.rd.autocode.domain.Employee;
import com.epam.rd.autocode.domain.FullName;
import com.epam.rd.autocode.domain.Position;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class SetMapperFactory {

    public SetMapper<Set<Employee>> employeesSetMapper() {
        return new SetMapper<Set<Employee>>() {

            @Override
            public Set<Employee> mapSet(ResultSet resultSet) {
                Map<BigInteger, Employee> employees = new HashMap<>();

                try {
                    while (resultSet.next()) {
                        Employee employee = new Employee(
                                new BigInteger(resultSet.getString("id")),
                                new FullName(resultSet.getString("firstName"), resultSet.getString("lastName"), resultSet.getString("middlename")),
                                Position.valueOf(resultSet.getString("position")),
                                resultSet.getDate("hiredate").toLocalDate(),
                                resultSet.getBigDecimal("salary"),
                                null
                        );
                        employees.put(employee.getId(), employee);
                    }

                    resultSet.beforeFirst();

                    while (resultSet.next()) {
                        Employee employee = employees.get(new BigInteger(resultSet.getString("id")));
                        String managerId = resultSet.getString("manager");
                        Employee manager = managerId != null ? employees.get(new BigInteger(managerId)) : null;

                        employees.put(employee.getId(), new Employee(
                                employee.getId(),
                                employee.getFullName(),
                                employee.getPosition(),
                                employee.getHired(),
                                employee.getSalary(),
                                manager
                        ));
                    }

                    return new HashSet<>(employees.values());
                } catch (SQLException e) {
                    return null;
                }
            }
        };
    }
}
