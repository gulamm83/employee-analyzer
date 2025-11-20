package com.company.analyzer.model;
  
import java.util.Objects;
  
/**  
 * Represents an employee in the organization.  
 * Immutable class containing employee information.  
 */  
public class Employee {  
    private final String id;  
    private final String firstName;  
    private final String lastName;  
    private final double salary;  
    private final String managerId;
  
    public Employee(String id, String firstName, String lastName, double salary, String managerId) {  
        this.id = Objects.requireNonNull(id, "Employee ID cannot be null");  
        this.firstName = Objects.requireNonNull(firstName, "First name cannot be null");  
        this.lastName = Objects.requireNonNull(lastName, "Last name cannot be null");  
        this.salary = salary;  
        this.managerId = managerId; // null for CEO
          
        if (salary < 0) {  
            throw new IllegalArgumentException("Salary cannot be negative");  
        }  
    }
  
    public String getId() {  
        return id;  
    }
  
    public String getFirstName() {  
        return firstName;  
    }
  
    public String getLastName() {  
        return lastName;  
    }
  
    public double getSalary() {  
        return salary;  
    }
  
    public String getManagerId() {  
        return managerId;  
    }
  
    public boolean isCeo() {  
        return managerId == null || managerId.isEmpty();  
    }
  
    public String getFullName() {  
        return firstName + " " + lastName;  
    }
  
    @Override  
    public boolean equals(Object o) {  
        if (this == o) return true;  
        if (o == null || getClass() != o.getClass()) return false;  
        Employee employee = (Employee) o;  
        return Objects.equals(id, employee.id);  
    }
  
    @Override  
    public int hashCode() {  
        return Objects.hash(id);  
    }
  
    @Override  
    public String toString() {  
        return "Employee{" +  
                "id='" + id + '\'' +  
                ", name='" + getFullName() + '\'' +  
                ", salary=" + salary +  
                ", managerId='" + managerId + '\'' +  
                '}';  
    }  
}  