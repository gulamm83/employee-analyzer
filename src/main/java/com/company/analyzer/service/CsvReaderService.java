package com.company.analyzer.service;
  
import com.company.analyzer.exception.EmployeeDataException;  
import com.company.analyzer.model.Employee;  
import com.company.analyzer.util.Constants;
  
import java.io.BufferedReader;  
import java.io.FileReader;  
import java.io.IOException;  
import java.nio.file.Files;  
import java.nio.file.Path;  
import java.util.ArrayList;  
import java.util.List;
  
/**  
 * Service responsible for reading employee data from CSV files.  
 */  
public class CsvReaderService {
  
    /**  
     * Reads employee data from a CSV file.  
     *  
     * @param filePath path to the CSV file  
     * @return list of employees  
     * @throws EmployeeDataException if file cannot be read or data is invalid  
     */  
    public List<Employee> readEmployees(String filePath) throws EmployeeDataException {  
        validateFilePath(filePath);
          
        List<Employee> employees = new ArrayList<>();  
        int lineNumber = 0;
  
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {  
            String line;  
            boolean isFirstLine = true;
  
            while ((line = reader.readLine()) != null) {  
                lineNumber++;
                  
                // Skip header line  
                if (isFirstLine) {  
                    isFirstLine = false;  
                    continue;  
                }
  
                // Skip empty lines  
                if (line.trim().isEmpty()) {  
                    continue;  
                }
  
                try {  
                    Employee employee = parseLine(line, lineNumber);  
                    employees.add(employee);  
                } catch (IllegalArgumentException e) {  
                    throw new EmployeeDataException(  
                        "Invalid data at line " + lineNumber + ": " + e.getMessage(), e);  
                }  
            }
  
            if (employees.isEmpty()) {  
                throw new EmployeeDataException("No employee data found in file");  
            }
  
            return employees;
  
        } catch (IOException e) {  
            throw new EmployeeDataException("Error reading file: " + filePath, e);  
        }  
    }
  
    /**  
     * Parses a single CSV line into an Employee object.  
     */  
    private Employee parseLine(String line, int lineNumber) {  
        String[] parts = line.split(Constants.CSV_DELIMITER, -1);
  
        if (parts.length != Constants.CSV_EXPECTED_COLUMNS) {  
            throw new IllegalArgumentException(  
                "Expected " + Constants.CSV_EXPECTED_COLUMNS + " columns, found " + parts.length);  
        }
  
        String id = parts[0].trim();  
        String firstName = parts[1].trim();  
        String lastName = parts[2].trim();  
        String salaryStr = parts[3].trim();  
        String managerId = parts[4].trim();
  
        if (id.isEmpty()) {  
            throw new IllegalArgumentException("Employee ID cannot be empty");  
        }
  
        if (firstName.isEmpty() || lastName.isEmpty()) {  
            throw new IllegalArgumentException("Employee name cannot be empty");  
        }
  
        double salary;  
        try {  
            salary = Double.parseDouble(salaryStr);  
        } catch (NumberFormatException e) {  
            throw new IllegalArgumentException("Invalid salary value: " + salaryStr);  
        }
  
        // Empty managerId is valid for CEO  
        String managerIdValue = managerId.isEmpty() ? null : managerId;
  
        return new Employee(id, firstName, lastName, salary, managerIdValue);  
    }
  
    /**  
     * Validates that the file path exists and is readable.  
     */  
    private void validateFilePath(String filePath) throws EmployeeDataException {  
        if (filePath == null || filePath.trim().isEmpty()) {  
            throw new EmployeeDataException("File path cannot be null or empty");  
        }
  
        Path path = Path.of(filePath);  
        if (!Files.exists(path)) {  
            throw new EmployeeDataException("File does not exist: " + filePath);  
        }
  
        if (!Files.isReadable(path)) {  
            throw new EmployeeDataException("File is not readable: " + filePath);  
        }  
    }  
}  